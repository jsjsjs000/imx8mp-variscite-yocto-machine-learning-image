#		Create new layer - meta-pco-ml
# https://www.phytec.eu/en/cdocuments/?doc=UIHsG#L813e-A12YoctoReferenceManualHeadHardknott-CreateyourownLayercreatelayer
#	"Create your own Layer"
bitbake-layers create-layer -e pco-ml meta-pco-ml
mv meta-pco-ml/ ../sources/
mv ../sources/meta-pco-ml/recipes-pco-ml/pco-ml/pco-ml_0.1.bb ../sources/meta-pco-ml/recipes-pco-ml/pco-ml/pco-ml.bb
mkdir ../sources/meta-pco-ml/recipes-pco-ml/pco-ml/pco-ml
code conf/bblayers.conf
# ----------------------------------------
BBLAYERS += "\
  ${OEROOT}/../meta-pco-ml \
# ----------------------------------------

bitbake pco-ml   # compile only this recipe


#		Create new image - pco-ml-image
# create meta-pco-ml image first
mkdir -p ../sources/meta-pco-ml/recipes-images/pco-ml-image

bitbake pco-ml-image


#		Ubuntu Fonts
# Download fonts: https://fonts.google.com/download/next-steps?query=ubuntu+mono
# copy fonts to: /usr/share/fonts/truetype/ubuntu-font-family/
unzip ~/Downloads/Ubuntu_Mono.zip -d ../sources/meta-pco-ml/recipes-pco-ml/pco-ml/pco-ml/ubuntu-font-family
cp /usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-Bold.ttf /usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-B.ttf
