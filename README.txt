#		Install Variscite Yocto PD23.1.0 for i.MX 8M plus on Ubuntu 22.04 host PC


#   Prepare Yocto
# Host PC Ubuntu 22.04
sudo apt install -y gawk wget git diffstat unzip texinfo gcc-multilib \
  build-essential chrpath socat cpio python3 python3-pip python3-pexpect \
  xz-utils debianutils iputils-ping libsdl1.2-dev xterm libyaml-dev libssl-dev \
  autoconf libtool libglib2.0-dev libarchive-dev \
  sed cvs subversion coreutils texi2html docbook-utils \
  help2man make gcc g++ desktop-file-utils libgl1-mesa-dev libglu1-mesa-dev \
  mercurial automake groff curl lzop asciidoc u-boot-tools dos2unix mtd-utils pv \
  libncurses5 libncurses5-dev libncursesw5-dev libelf-dev zlib1g-dev bc rename \
  zstd libgnutls28-dev pv
sudo apt install -y python3-git liblz4-tool python3-jinja2 python3-subunit locales libacl1 python-is-python3

git config --list
git config --global user.name "<username>"
git config --global user.email "<email>"

curl https://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
export PATH=~/bin:$PATH

#   optional: mount another disk (PCO PC Ubuntu)
mkdir /mnt/ubuntu/
sudo nano /etc/fstab
# ----------------------------------------
/dev/sdc6  /mnt/ubuntu  ext4  defaults  0  1
# ----------------------------------------

#   optional: symbolic link to other disk
mkdir /mnt/ubuntu/var-fsl-yocto
ln -s /mnt/ubuntu/var-fsl-yocto/ ~/var-fsl-yocto

mkdir -p ~/var-fsl-yocto
cd ~/var-fsl-yocto

repo init -u https://github.com/varigit/variscite-bsp-platform.git -b kirkstone -m kirkstone-5.15.71-2.2.0.xml
repo sync -j$(nproc)

MACHINE=imx8mp-var-dart DISTRO=fslc-xwayland . var-setup-release.sh build_xwayland
#> q, y, Enter

#   Download and install Basler meta layers for cameras
# https://variwiki.com/index.php?title=MX8_Basler_Camera&release=mx8mp-yocto-kirkstone-5.15.71_2.2.0-v1.3
git clone https://github.com/varigit/meta-basler-imx8 -b kirkstone-5.15.71-2.2.0 ../sources/meta-basler-imx8
git clone https://github.com/basler/meta-basler-tools -b kirkstone ../sources/meta-basler-tools

nano conf/bblayers.conf
# ----------------------------------------
BBLAYERS += " ${BSPDIR}/sources/meta-basler-imx8 "
BBLAYERS += " ${BSPDIR}/sources/meta-basler-tools "
# ----------------------------------------

nano conf/local.conf
# ----------------------------------------
ACCEPT_BASLER_EULA = "1"
IMAGE_INSTALL:append = " packagegroup-dart-bcon-mipi"
# ----------------------------------------

#		Add meta layer to Yocto layers
git clone https://github.com/jsjsjs000/imx8mp-phytec-yocto-machine-learning-image.git ../sources/meta-pco-ml

code conf/bblayers.conf
# ----------------------------------------
BBLAYERS += "\
  ${BSPDIR}/sources/meta-pco-ml \
# ----------------------------------------

#   Compile Yocto image
bitbake pco-ml-image

#   Setup Yocto environment for next time compile image
cd ~/var-fsl-yocto
source setup-environment build_xwayland
bitbake pco-ml-image

#   Write to SD card
ll tmp/deploy/images/imx8mp-var-dart/pco-ml-image-imx8mp-var-dart.wic.gz
ls tmp/work/imx8mp_var_dart-fslc-linux/pco-ml-image/1.0-r0/rootfs/

lsblk -e7
#> sdd      8:48   1  59,5G  0 disk  # in this case /dev/sdd

sync; umount /media/$USER/boot; umount /media/$USER/root
sd_card=/dev/sdX  # <--------------------------- set your SD card - in this case /dev/sdd from lsblk
zcat tmp/deploy/images/imx8mp-var-dart/pco-ml-image-imx8mp-var-dart.wic.gz | pv | sudo dd of=${sd_card} bs=1M conv=fsync; sync

#   Optional: Fix deny access to git tinycompress
# alternative git for tinycompress
# change: git://git.alsa-project.org/tinycompress
# to: https://github.com/alsa-project/tinycompress.git

nano ../sources/meta-freescale/recipes-multimedia/tinycompress/tinycompress_1.1.6.bb
# ----------------------------------------
# SRC_URI = "https://github.com/alsa-project/tinycompress.git;protocol=http
SRC_URI[sha256sum] = "2cb5ad8d27a5a8896904a31fec99d91fde251bcbeb620b60e75a7bd49e6d9abd"
SRC_URI = "https://github.com/alsa-project/tinycompress/archive/refs/tags/v${PV}.tar.gz \
S = "${WORKDIR}/tinycompress-${PV}"
# ----------------------------------------

#   Optional: Fix not enough RAM for nodejs (16 CPU cores / 16GB RAM)
nano conf/local.conf
# ----------------------------------------
PARALLEL_MAKE = "-j 4"     # maximum CPU cores
# BB_NUMBER_THREADS = "4"  # maximum paralell Yocto tasks
# ----------------------------------------

#   Set static IP address
ifconfig eth0 192.168.3.11  # set temporary IP address

ssh root@192.168.3.11       # connect to i.MX via SSH

# https://variwiki.com/index.php?title=Static_IP_Address#Using_NetworkManager
nmcli con add type ethernet ifname eth0 con-name static-eth0 ip4 192.168.3.11/24 gw4 192.168.3.10
nmcli con mod static-eth0 ipv4.dns "10.0.5.1"
nmcli con up static-eth0

cat /etc/NetworkManager/system-connections/static-eth0.nmconnection

# not tested - or reboot
systemctl restart systemd-networkd
journalctl --unit=systemd-networkd.service

