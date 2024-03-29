SUMMARY = "Xfce4 Widget library and X Window System interaction"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "intltool-native perl-native gtk+ gtk+3 libice libsm libxfce4util xfce4-dev-tools xfconf virtual/libx11"

inherit xfce gtk-doc gobject-introspection distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
    file://0001-libxfce4kbd-private-xfce4-keyboard-shortcuts.xml-fix.patch \
"
SRC_URI[md5sum] = "9e5a805d2d557df79e571468978a2766"
SRC_URI[sha256sum] = "3c5e505ce56a5993db473efed9962705dd255ffaee17009ed1b3e9f40b543969"

EXTRA_OECONF += "--with-vendor-info=${DISTRO}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gladeui] = "--enable-gladeui,--disable-gladeui,glade3"
PACKAGECONFIG[gladeui2] = "--enable-gladeui2,--disable-gladeui2,glade"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

PACKAGES =+ "${PN}-gtk2 ${PN}-gtk3"
FILES_${PN}-gtk2 += "${libdir}/libxfce4ui-1.so.* ${libdir}/libxfce4kbd-private-2.so.*"
FILES_${PN}-gtk3 += "${libdir}/libxfce4ui-2.so.* ${libdir}/libxfce4kbd-private-3.so.*"

PACKAGES += "${PN}-glade"
FILES_${PN}-glade = " \
    ${libdir}/glade \
    ${libdir}/glade3 \
    ${datadir}/glade \
    ${datadir}/glade3 \
"

RDEPENDS_${PN}-gtk2 = "${PN}"
RDEPENDS_${PN}-gtk3 = "${PN}"
