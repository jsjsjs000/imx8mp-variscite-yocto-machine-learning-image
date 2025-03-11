DESCRIPTION = "PCO image for testing Vision4CE machine learning."
LICENSE = "MIT"

require recipes-fsl/images/fsl-image-gui.bb

inherit features_check populate_sdk_qt5

CONFLICT_DISTRO_FEATURES = "directfb"

# Install fonts
QT5_FONTS = " \
    ttf-dejavu-mathtexgyre \
    ttf-dejavu-sans \
    ttf-dejavu-sans-condensed \
    ttf-dejavu-sans-mono \
    ttf-dejavu-serif \
    ttf-dejavu-serif-condensed \
"

# Install QT5 demo applications
QT5_IMAGE_INSTALL = " \
    packagegroup-qt5-demos \
    ${QT5_FONTS} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'qtwayland qtwayland-plugins', '', d)} \
"

QT5_IMAGE_INSTALL:append:imxgpu3d = " \
    packagegroup-qt5-3d \
"

# uncomment the following line to add webengine support
# but remind to add also meta-python2 to the bblayere
# QT5_IMAGE_INSTALL:append = " packagegroup-qt5-webengine"

# uncomment the following line to add webkit support
# but remind that is considered obsolete since Qt 5.7
# QT5_IMAGE_INSTALL:append = " packagegroup-qt5-webkit"


IMAGE_INSTALL += " \
    ${QT5_IMAGE_INSTALL} \
    qtserialbus \
"

# Due to the Qt samples the resulting image will not fit the default NAND size.
# Removing default ubi creation for this image
IMAGE_FSTYPES:remove = "multiubi"

# ----------------------------------------
IMAGE_INSTALL:append = "\
	htop iotop apt zip xz mc nano lsof tcpdump netcat socat screen tree minicom openvpn \
	openssl libmcrypt \
	python3 python3-pip python3-requests python3-fcntl python3-pygobject \
	opencv opencv-samples opencv-apps python3-opencv \
	gstreamer1.0 gstreamer1.0-plugins-good gstreamer1.0-plugins-bad \
	pco-ml \
	proftpd uftp vsftpd \
"
#   gstreamer1.0-plugins-bad-opencv

IMAGE_INSTALL:append = " gstreamer1.0-rtsp-server glfw boost freetype liberation-fonts"
PACKAGECONFIG:append:pn-glfw = " wayland"
