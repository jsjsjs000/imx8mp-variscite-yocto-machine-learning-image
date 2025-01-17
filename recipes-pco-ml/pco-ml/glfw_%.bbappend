DEPENDS = "libpng libglu zlib"
REQUIRED_DISTRO_FEATURES = "opengl"

#   not works:
# DEPENDS:remove = "libxrandr libxinerama libxi libxcursor"
# REQUIRED_DISTRO_FEATURES:remove = "x11"

ANY_OF_DISTRO_FEATURES = "wayland x11"
PACKAGECONFIG ??= "x11"
PACKAGECONFIG[wayland] = "-DGLFW_USE_WAYLAND=ON,,wayland wayland-native wayland-protocols extra-cmake-modules libxkbcommon"
PACKAGECONFIG[x11] = ",,libxrandr libxinerama libxi libxcursor"
