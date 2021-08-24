
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
DESCRIPTION = "i.MX U-Boot suppporting SRG-IM8P boards."
UBOOT_SRC = "gitsm://github.com/KunYi/uboot-imx.git;protocol=https"
SRCBRANCH = "srg-im8p_v2021.04"
SRCREV = "4ed1ae03eb3dc7bc26f3d6a6d47778f46545982f"
SRC_URI = "${UBOOT_SRC};branch=${SRCBRANCH} \
	file://fw_env.config \
"

do_configure_prepend_mx8mp() {
    # check secure boot is enabled to apply CONFIG_IMX_HAB on configure file
    if [ "x${ENABLED_SECURE_BOOT}" = "xyes" ]; then
        if [ -n "${UBOOT_CONFIG}" ]; then
            unset i j
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]; then
                        echo "CONFIG_IMX_HAB=y" >> ${S}/configs/${config}
                    fi
                done
                unset  j
            done
            unset  i
        fi
    fi
}

do_install_append_mx8mp () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    #
    install -d ${D}/boot/extlinux
    install -m 0644 ${B}/extlinux.conf ${D}/boot/extlinux/extlinux.conf
}
