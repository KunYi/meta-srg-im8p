# Copyright 2021 UWINGS
# Copyright (C) 2015 Freescale Semiconductor
# Copyright 2017-2019 NXP
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-fsl/images/imx-image-multimedia.bb

CONFLICT_DISTRO_FEATURES = "directfb"

#===========================================================================================================
# start for generation .swu file
#===========================================================================================================

# remove comment
#inherit swupdate-image

SRC_URI += " \
    file://sw-description \
"

SWUPDATE_IMAGES_FSTYPES[srg-im8p-image] = ".ext4.zst"

RFS_VERSION ?= "1.0.1"
SWU_PRIVATE_KEY ?= "${TOPDIR}/../swu-keys/priv.pem"
SWU_PRIVATE_KEY_PASSWORD ?= "${TOPDIR}/../swu-keys/passout"
SWU_SOFTWARE_VERSION ??= "1.0.0"

python () {
    # detect in docker
    if os.path.exists("/.dockerenv"):
        d.setVar("SWUPDATE_PRIVATE_KEY", d.getVar("SWU_PRIVATE_KEY").replace("build/..", "repo"))
        d.setVar("SWUPDATE_PASSWORD_FILE", d.getVar("SWU_PRIVATE_KEY_PASSWORD").replace("build/..", "repo"))
}

SWUPDATE_SIGNING = "RSA"
SWUPDATE_PRIVATE_KEY = "${SWU_PRIVATE_KEY}"
SWUPDATE_PASSWORD_FILE = "${SWU_PRIVATE_KEY_PASSWORD}"
#===========================================================================================================
# end for generation .swu file
#===========================================================================================================

# Add machine learning for certain SoCs
ML_PKGS                   ?= ""
ML_STATICDEV              ?= ""
ML_PKGS_mx8                = "packagegroup-imx-ml"
ML_STATICDEV_mx8           = ""
ML_PKGS_mx8dxl             = ""
ML_STATICDEV_mx8dxl        = ""
ML_PKGS_mx8phantomdxl      = ""
ML_STATICDEV_mx8phantomdxl = ""
ML_PKGS_mx8mnlite          = ""
ML_STATICDEV_mx8mnlite     = ""

TOOLCHAIN_TARGET_TASK += " \
    ${ML_STATICDEV} \
"

# Add opencv for i.MX GPU
OPENCV_PKGS       ?= ""
OPENCV_PKGS_imxgpu = " \
    opencv-apps \
    opencv-samples \
    python3-opencv \
"

SWUPDATE_PKGS = " \
    swupdate \
    swupdate-lua \
    swupdate-progress \
    swupdate-tools \
    swupdate-usb \
    swupdate-tools-hawkbit \
    libubootenv-bin \
"

FILESYSTEM_PKGS = " \
    fuse-exfat \
    ntfs-3g-ntfsprogs \
    exfat-utils \
    aufs-util \
"
IMAGE_INSTALL += " \
    ${OPENCV_PKGS} \
    ${ML_PKGS} \
    ${SWUPDATE_PKGS} \
    ${FILESYSTEM_PKGS} \
    packagegroup-security-tpm2 \
    tzdata \
    kernel-devsrc \
    device-init \
    datawipe \
    stressapptest \
    networkmanager \
    networkmanager-nmtui \
    modemmanager \
"

TOOLCHAIN_TARGET_TASK += " \
    ${ML_STATICDEV} \
"

add_boot_files() {
	echo "add boot files"
	install -m 0644 "${DEPLOY_DIR_IMAGE}/srg-im8p.dtb" "${IMAGE_ROOTFS}/boot/srg-im8p.dtb"
	install -m 0644 "${DEPLOY_DIR_IMAGE}/initrd-srg-im8p.img" "${IMAGE_ROOTFS}/boot/initrd.img"
	# install -d 0777 "${IMAGE_ROOTFS}/boot/extlinux"
	# install -m 0644 "${DEPLOY_DIR_IMAGE}/extlinux.conf-${MACHINE}" "${IMAGE_ROOTFS}/boot/extlinux/extlinux.conf"
}

ROOTFS_POSTPROCESS_COMMAND += "add_boot_files;"
