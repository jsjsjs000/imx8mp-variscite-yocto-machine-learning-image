SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

#DEPENDS += " freetype liberation-fonts"
RDEPENDS:${PN} += " freetype liberation-fonts"

FILESEXTRAPATHS:prepend := "${THISDIR}/pco-ml:"
SRC_URI = " \
	file://ubuntu-font-family/UbuntuMono-Regular.ttf \
	file://ubuntu-font-family/UbuntuMono-Bold.ttf \
	file://ubuntu-font-family/UbuntuMono-BoldItalic.ttf \
	file://ubuntu-font-family/UbuntuMono-Italic.ttf \
	file://ubuntu-font-family/UFL.txt \
"

do_install() {
	install -d ${D}/usr/share/fonts/truetype/liberation2/
	ln -sr ${D}/usr/share/fonts/ttf/LiberationMono-Bold.ttf ${D}/usr/share/fonts/truetype/liberation2/LiberationMono-Bold.ttf

	# Download fonts: https://fonts.google.com/download/next-steps?query=ubuntu+mono
	install -d ${D}/usr/share/fonts/truetype/ubuntu-font-family/
	install -m 0644 ${WORKDIR}/ubuntu-font-family/UbuntuMono-Regular.ttf ${D}/usr/share/fonts/truetype/ubuntu-font-family/
	install -m 0644 ${WORKDIR}/ubuntu-font-family/UbuntuMono-Bold.ttf ${D}/usr/share/fonts/truetype/ubuntu-font-family/
	install -m 0644 ${WORKDIR}/ubuntu-font-family/UbuntuMono-BoldItalic.ttf ${D}/usr/share/fonts/truetype/ubuntu-font-family/
	install -m 0644 ${WORKDIR}/ubuntu-font-family/UbuntuMono-Italic.ttf ${D}/usr/share/fonts/truetype/ubuntu-font-family/
	install -m 0644 ${WORKDIR}/ubuntu-font-family/UFL.txt ${D}/usr/share/fonts/truetype/ubuntu-font-family/

	install -m 0644 ${WORKDIR}/ubuntu-font-family/UbuntuMono-Bold.ttf ${D}/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-B.ttf
}

FILES:${PN} += "/usr/share/fonts/truetype/liberation2/LiberationMono-Bold.ttf"
FILES:${PN} += "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-Regular.ttf"
FILES:${PN} += "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-Bold.ttf"
FILES:${PN} += "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-BoldItalic.ttf"
FILES:${PN} += "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-Italic.ttf"
FILES:${PN} += "/usr/share/fonts/truetype/ubuntu-font-family/UFL.txt"
FILES:${PN} += "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-B.ttf"
