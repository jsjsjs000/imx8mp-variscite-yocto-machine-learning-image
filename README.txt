#		Install Phytec Yocto PD23.1.0 for i.MX 8M plus on Ubuntu 22.04 host PC
# Install in ~/phyLinux folder
mkdir ~/phyLinux
cd ~/phyLinux
wget https://download.phytec.de/Software/Linux/Yocto/Tools/phyLinux
# chmod +x phyLinux
python phyLinux init
#> colors: y
#> 13: imx8mp
#> 42: BSP-Yocto-NXP-i.MX8MP-PD23.1.0
#> 5: phyboard-pollux-imx8mp-3: PHYTEC phyBOARD-Pollux i.MX8M Plus 1-4GB RAM
#>                              target: phytec-qt6demo-image

code build/conf/local.conf
# change <user> to your user name in /home/ folder
----------------------------------------
# don't use ~ - use absolute paths
DL_DIR ?= "/home/<user>/phyLinux/yocto_downloads"
SSTATE_DIR ?= "/home/<user>/phyLinux/yocto_sstate"
ACCEPT_FSL_EULA = "1"
----------------------------------------

mkdir ~/phyLinux/yocto_downloads
mkdir ~/phyLinux/yocto_sstate


#		Initialize Yocto environment
cd ~/phyLinux
source sources/poky/oe-init-build-env


#		Copy or download meta layer to Phytec Yocto PD23.1.0 for i.MX 8M plus
# assume Phytec Yocto PD23.1.0 is in ~/phyLinux
cd ~/phyLinux/sources/
# mkdir -p ~/phyLinux/sources/meta-pco-ml/
git clone https://github.com/jsjsjs000/imx8mp-phytec-yocto-machine-learning-image
mv imx8mp-phytec-yocto-machine-learning-image/ meta-pco-ml/
cd -


#		Add meta layer to Yocto layers
code conf/bblayers.conf
# ----------------------------------------
BBLAYERS += "\
  ${OEROOT}/../meta-pco-ml \
# ----------------------------------------


#		Fix compilation problem: arm-compute-library_22.05.bb - PD23.1.0 - Ubuntu 22.04 host PC
# when add packagegroup-imx-ml meta layer
IMAGE_INSTALL += " packagegroup-imx-ml"

code ../sources/poky/meta/classes/scons.bbclass
# comment out:
# EXTRA_OESCONS:append = " ${SCONS_MAXLINELENGTH}"
# remove 3 times: PREFIX=${prefix} prefix=${prefix}


#		Compile Yocto image
bitbake pco-ml-image


#		Write image to SD card
# List SD card devices in your PC
lsblk -e7
#> NAME                  MAJ:MIN RM   SIZE RO TYPE  MOUNTPOINTS
#> mmcblk0               179:0    0  29,5G  0 disk                <------------ SD card in your PC
#> ├─mmcblk0p1           179:1    0    65M  0 part  /media/user/boot
#> └─mmcblk0p2           179:2    0   3,9G  0 part  /media/user/root

# Find your SD card in system - /dev/mmcblk0 in this case

# Unmount SD card - Ubuntu by default mount SD card after plug in
sync; umount /media/$USER/boot; umount /media/$USER/root

# Install progressbar program for dd
sudo apt install -y pv

# Write image to SD card in /dev/mmcblk0
sudo pv -tpreb deploy/images/phyboard-pollux-imx8mp-3/pco-ml-image-phyboard-pollux-imx8mp-3.wic | sudo dd of=/dev/mmcblk0 bs=1M oflag=sync; sync

# Wait few minutes
#> 981MiB 0:00:42 [24,3MiB/s] [====================>                                 ] 39% ETA 0:01:05

# After finish write image to SD card
#> coppied 2634668032 bytes (2,6 GB, 2,5 GiB), 106,465 s, 24,7 MB/s

# Remove SD card from reader
# Insert SD card to Phytec i.MX 8M Plus devboard
# Set devboard i.MX boot source on DIP switch (near USB Debug connector, reset and power switch - left bottom corner)
#   set as SD card: 1000 (1: ON 2: OFF 3: OFF 4: OFF)
# Connect 2 cameras Phytec VM-017 AR0521 to CSI1 and CSI2 connector
# Connect Ethernet cable to RJ-45 connector
# Connect any FullHD monitor to HDMI connector
# Optional: connect mouse and keyboard to USB ports
# Optional: connect microUSB to USB Debug connector - Linux serial debug console
#           connect by Ubuntu Terminal command: minicom -w -D /dev/ttyUSB0  # 115200 b/s

# Connect 12V to devboard - power on devboard (ON/OFF switch)
# Wait 20 seconds for start up Linux

# Set your Ethernet card in PC: IP: 192.168.3.10, Mask: 255.255.255.0
# Connect to devboard by SSH - IP: 192.168.3.11
ssh root@192.168.3.11
# User: root, password: <empty>

# Test GStreamer
gst-launch-1.0 videotestsrc ! ximagesink display=:0
# Press: Ctrl+C to abort

# Check file /boot/bootenv.txt - for 2 cameras AR0521
# overlays=imx8mp-isi-csi1.dtbo imx8mp-vm017-csi1.dtbo imx8mp-isi-csi2.dtbo imx8mp-vm017-csi2.dtbo

# Setup 2 cameras resolution
setup-pipeline-csi1 -f SGRBG8_1X8 -s 1920x1080 -o '(336,432)' -c 1920x1080
setup-pipeline-csi2 -f SGRBG8_1X8 -s 1920x1080 -o '(336,432)' -c 1920x1080

# Setup camera brightness (camera exposition and gain)
v4l2-ctl -d /dev/cam-csi1 -c exposure=3000,analogue_gain=8000,digital_gain_red=1400,digital_gain_blue=1400,dynamic_defect_pixel_correction=1
v4l2-ctl -d /dev/cam-csi2 -c exposure=3000,analogue_gain=8000,digital_gain_red=1400,digital_gain_blue=1400,dynamic_defect_pixel_correction=1

# Test camera 1 on GStreamer
gst-launch-1.0 -v v4l2src device=/dev/video0 ! video/x-bayer,format=grbg,depth=8,width=1920,height=1080,framerate=60/1,pixel-aspect-ratio=1/1 ! bayer2rgbneon ! ximagesink display=:0 sync=false
# Press: Ctrl+C to abort

# Test camera 2 on GStreamer
gst-launch-1.0 -v v4l2src device=/dev/video1 ! video/x-bayer,format=grbg,depth=8,width=1920,height=1080,framerate=60/1,pixel-aspect-ratio=1/1 ! bayer2rgbneon ! ximagesink display=:0 sync=false
# Press: Ctrl+C to abort

# Test camera 1 on GStreamer with display FPS
gst-launch-1.0 -v v4l2src device=/dev/video0 ! video/x-bayer,format=grbg,depth=8,width=1920,height=1080,framerate=60/1,pixel-aspect-ratio=1/1 ! bayer2rgbneon ! fpsdisplaysink sync=false
# Press: Ctrl+C to abort

# Composite 2 streams: camera1 60fps + camera2 60fps, result 60fps - CPU 1.5-2 cores (150-200%)
gst-launch-1.0 imxcompositor_g2d name=comp sink_1::xpos=0 sink_1::ypos=0 sink_1::alpha=0.5 ! \
  waylandsink window-width=1920 window-height=1080 \
  v4l2src device=/dev/video0 ! video/x-bayer,format=grbg,depth=8,width=1920,height=1080,framerate=60/1,pixel-aspect-ratio=1/1 ! bayer2rgbneon ! comp.sink_0 \
  v4l2src device=/dev/video1 ! video/x-bayer,format=grbg,depth=8,width=1920,height=1080,framerate=60/1,pixel-aspect-ratio=1/1 ! bayer2rgbneon ! comp.sink_1

# Composite 2 streams: camera1 60fps + camera2 60fps, result 60fps - CPU 1.5-2 cores (150-200%)
gst-launch-1.0 imxcompositor_g2d name=comp \
  sink_0::xpos=0 sink_0::ypos=0 sink_0::width=960 sink_0::height=540 \
	sink_1::xpos=960 sink_1::ypos=0 sink_1::width=960 sink_1::height=540 ! \
  waylandsink window-width=1920 window-height=1080 \
  v4l2src device=/dev/video0 ! video/x-bayer,format=grbg,depth=8,width=1920,height=1080,framerate=60/1,pixel-aspect-ratio=1/1 ! bayer2rgbneon ! comp.sink_0 \
  v4l2src device=/dev/video1 ! video/x-bayer,format=grbg,depth=8,width=1920,height=1080,framerate=60/1,pixel-aspect-ratio=1/1 ! bayer2rgbneon ! comp.sink_1

# Power off devboard Linux command
poweroff

# More information - Phytec documentation:
# https://www.phytec.eu/en/produkte/single-board-computer/phyboard-pollux/#downloads/
# https://phytec.github.io/doc-bsp-yocto/bsp/imx8/imx8mp/pd23.1.0.html
# https://www.phytec.de/cdocuments/?doc=d4A0G
# https://www.phytec.de/cdocuments/?doc=IoBsLg
# https://www.phytec.de/cdocuments/?doc=l4CqLw
