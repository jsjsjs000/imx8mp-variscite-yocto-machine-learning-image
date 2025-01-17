code ~/phyLinux/sources/meta-qt6-phytec/recipes-images/images/phytec-qt6demo-image.bb
# ----------------------------------------
IMAGE_INSTALL:append = " gstreamer1.0-rtsp-server glfw boost freetype liberation-fonts"
PACKAGECONFIG:append:pn-glfw = " wayland"
# ----------------------------------------

code ~/phyLinux/sources/meta-openembedded/meta-oe/recipes-core/glfw/glfw_%.bbappend
# ----------------------------------------
DEPENDS = "libpng libglu zlib"
REQUIRED_DISTRO_FEATURES = "opengl"

#  not works:
#DEPENDS:remove = "libxrandr libxinerama libxi libxcursor"
#REQUIRED_DISTRO_FEATURES:remove = "x11"

ANY_OF_DISTRO_FEATURES = "wayland x11"
PACKAGECONFIG ??= "x11"
PACKAGECONFIG[wayland] = "-DGLFW_USE_WAYLAND=ON,,wayland wayland-native wayland-protocols extra-cmake-modules libxkbcommon"
PACKAGECONFIG[x11] = ",,libxrandr libxinerama libxi libxcursor"
# ----------------------------------------

# Adding fonts on the destination
mkdir -p /usr/share/fonts/truetype/liberation2
ln -s /usr/share/fonts/ttf/LiberationMono-Bold.ttf /usr/share/fonts/truetype/liberation2/LiberationMono-Bold.ttf

# Download fonts: https://fonts.google.com/download/next-steps?query=ubuntu+mono
mkdir -p /usr/share/fonts/truetype/ubuntu-font-family/
# copy fonts to: /usr/share/fonts/truetype/ubuntu-font-family/
cp /usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-Bold.ttf /usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-B.ttf
