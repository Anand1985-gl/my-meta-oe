SUMMARY = "Hardware health monitoring applications"
HOMEPAGE = "https://hwmon.wiki.kernel.org/"
LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    bison-native \
    flex-native \
    rrdtool \
    virtual/libiconv \
"

SRC_URI = "git://github.com/lm-sensors/lm-sensors.git;protocol=https \
           file://fancontrol.init \
           file://sensord.init \
           file://0001-lmsensors-sensors-detect-print-a-special-message-whe.patch \
           file://0002-lm-sensors-fix-sensors-detect-can-t-read-the-cpu-inf.patch \
"
SRCREV = "e8afbda10fba571c816abddcb5c8180afc435bba"

inherit update-rc.d systemd

RDEPENDS_${PN}-dev = ""

INITSCRIPT_PACKAGES = "${PN}-fancontrol ${PN}-sensord"
INITSCRIPT_NAME_${PN}-fancontrol = "fancontrol"
INITSCRIPT_NAME_${PN}-sensord = "sensord"
INITSCRIPT_PARAMS_${PN}-fancontrol = "defaults 66"
INITSCRIPT_PARAMS_${PN}-sensord = "defaults 67"

SYSTEMD_PACKAGES = "${PN}-sensord"
SYSTEMD_SERVICE_${PN}-sensord = "sensord.service lm_sensors.service fancontrol.service"
SYSTEMD_AUTO_ENABLE = "disable"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = 'EXLDFLAGS="${LDFLAGS}" \
        MACHINE=${TARGET_ARCH} PREFIX=${prefix} MANDIR=${mandir} \
        LIBDIR=${libdir} \
        CC="${CC}" AR="${AR}"'

do_compile() {
    sed -i -e 's:^# \(PROG_EXTRA\):\1:' ${S}/Makefile
    # Respect LDFLAGS
    sed -i -e 's/\$(LIBDIR)$/\$(LIBDIR) \$(LDFLAGS)/g' ${S}/Makefile
    sed -i -e 's/\$(LIBSHSONAME) -o/$(LIBSHSONAME) \$(LDFLAGS) -o/g' \
                ${S}/lib/Module.mk
    oe_runmake user PROG_EXTRA="sensors sensord"
}

do_install() {
    oe_runmake user_install DESTDIR=${D}
    install -m 0755 ${S}/prog/sensord/sensord ${D}${sbindir}
    install -m 0644 ${S}/prog/sensord/sensord.8 ${D}${mandir}/man8

    # Install directory
    install -d ${D}${INIT_D_DIR}

    # Install fancontrol init script
    install -m 0755 ${WORKDIR}/fancontrol.init ${D}${INIT_D_DIR}/fancontrol

    # Install sensord init script
    install -m 0755 ${WORKDIR}/sensord.init ${D}${INIT_D_DIR}/sensord

    # Insall sensord service script
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/prog/init/*.service ${D}${systemd_system_unitdir}
}

RPROVIDES_${PN}-dbg += "${PN}-libsensors-dbg ${PN}-sensors-dbg ${PN}-sensord-dbg ${PN}-isatools-dbg"

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += " \
    ${PN}-libsensors \
    ${PN}-sensors \
    ${PN}-sensord \
    ${PN}-fancontrol \
    ${PN}-sensorsdetect \
    ${PN}-sensorsconfconvert \
    ${PN}-pwmconfig \
    ${PN}-isatools \
"

# libsensors packages
PACKAGES =+ "${PN}-libsensors ${PN}-libsensors-dev ${PN}-libsensors-staticdev ${PN}-libsensors-doc"

# sensors command packages
PACKAGES =+ "${PN}-sensors ${PN}-sensors-doc"

# sensord logging daemon
PACKAGES =+ "${PN}-sensord ${PN}-sensord-doc"

# fancontrol script
PACKAGES =+ "${PN}-fancontrol ${PN}-fancontrol-doc"

# sensors-detect script
PACKAGES =+ "${PN}-sensorsdetect ${PN}-sensorsdetect-doc"

# sensors-conf-convert script
PACKAGES =+ "${PN}-sensorsconfconvert ${PN}-sensorsconfconvert-doc"

# pwmconfig script
PACKAGES =+ "${PN}-pwmconfig ${PN}-pwmconfig-doc"

# isadump and isaset helper program
PACKAGES =+ "${PN}-isatools ${PN}-isatools-doc"


# libsensors files
FILES_${PN}-libsensors = "${libdir}/libsensors.so.* ${sysconfdir}/sensors3.conf ${sysconfdir}/sensors.d"
FILES_${PN}-libsensors-dev = "${libdir}/libsensors.so ${includedir}"
FILES_${PN}-libsensors-staticdev = "${libdir}/libsensors.a"
FILES_${PN}-libsensors-doc = "${mandir}/man3"
RRECOMMENDS_${PN}-libsensors = "lmsensors-config-libsensors"

# sensors command files
FILES_${PN}-sensors = "${bindir}/sensors"
FILES_${PN}-sensors-doc = "${mandir}/man1 ${mandir}/man5"
RDEPENDS_${PN}-sensors = "${PN}-libsensors"

# sensord logging daemon
FILES_${PN}-sensord = "${sbindir}/sensord ${INIT_D_DIR}/sensord ${systemd_system_unitdir}/sensord.service"
FILES_${PN}-sensord-doc = "${mandir}/man8/sensord.8"
RDEPENDS_${PN}-sensord = "${PN}-sensors rrdtool"
RRECOMMENDS_${PN}-sensord = "lmsensors-config-sensord"

# fancontrol script files
FILES_${PN}-fancontrol = "${sbindir}/fancontrol ${INIT_D_DIR}/fancontrol"
FILES_${PN}-fancontrol-doc = "${mandir}/man8/fancontrol.8"
RDEPENDS_${PN}-fancontrol = "bash"
RRECOMMENDS_${PN}-fancontrol = "lmsensors-config-fancontrol"

# sensors-detect script files
FILES_${PN}-sensorsdetect = "${sbindir}/sensors-detect"
FILES_${PN}-sensorsdetect-doc = "${mandir}/man8/sensors-detect.8"
RDEPENDS_${PN}-sensorsdetect = "${PN}-sensors perl perl-modules"

# sensors-conf-convert script files
FILES_${PN}-sensorsconfconvert = "${bindir}/sensors-conf-convert"
FILES_${PN}-sensorsconfconvert-doc = "${mandir}/man8/sensors-conf-convert.8"
RDEPENDS_${PN}-sensorsconfconvert = "${PN}-sensors perl perl-modules"

# pwmconfig script files
FILES_${PN}-pwmconfig = "${sbindir}/pwmconfig"
FILES_${PN}-pwmconfig-doc = "${mandir}/man8/pwmconfig.8"
RDEPENDS_${PN}-pwmconfig = "${PN}-fancontrol"

# isadump and isaset helper program files
FILES_${PN}-isatools = "${sbindir}/isa*"
FILES_${PN}-isatools-doc = "${mandir}/man8/isa*"
