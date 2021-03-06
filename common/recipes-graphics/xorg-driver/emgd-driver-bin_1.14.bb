SUMMARY = "EMGD 1.14 xserver binaries"
DESCRIPTION = "EMGD 1.14 includes some userspace binaries that use non-free \
licensing, which are now available via a non-click-through downloadable \
tarball, and is what this recipe now uses.  Since it is a non-free license, \
this recipe is marked as 'License_emgd-driver-bin_1.14' and you need to add \
to LICENSE_FLAGS_WHITELIST += \"license_emgd-driver-bin_1.14\" to your \
local.conf in order to enable it in a build."
LICENSE = "Intel-software-license-emgd-1.14 & Intel-user-space-graphics-driver-binary-license-emgd-1.14"
LICENSE_FLAGS = "license_${PN}_${PV}"
PR = "r2"

EMGD_LIC_DIR = "IEMGD_HEAD_Linux/License"
EMGD_RPM_DIR = "IEMGD_HEAD_Linux/MeeGo1.2"
EMGD_VIDEO_PLUGIN_DIR = "../common/video_plugin"

LIC_FILES_CHKSUM = "file://${WORKDIR}/${EMGD_LIC_DIR}/License.txt;md5=b54f01caaf8483b3cb60c0c40f2bf22d \
                    file://${WORKDIR}/${EMGD_LIC_DIR}/readme.txt;md5=73cbec7a0d2bc22faf567238e055bfc8"

DEPENDS = "rpm-native xz-native"
RDEPENDS = "libxcb-dri2"

SRC_URI = "https://edc.intel.com/Download.aspx?id=6190;downloadfilename=LIN_IEMGD_1_14_GOLD_2443.tgz"

SRC_URI[md5sum] = "733a7f237ffce21238ce2c9956df4fd6"
SRC_URI[sha256sum] = "bcdc333b5edbda7c746a83ef821ded4a0ca55ead30980e4e3680cdb6469f45a2"

# make sure generated rpm packages get non conflicting names
PKG_${PN} = "emgd-driver"
PKG_${PN}-dev = "emgd-driver-dev"
PKG_${PN}-dbg = "emgd-driver-dbg"
PKG_${PN}-doc = "emgd-driver-doc"

# These are closed binaries generated elsewhere so don't check ldflags
INSANE_SKIP_${PN} = "ldflags"
# Inhibit warnings about files being stripped, we can't do anything about it.
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

FILES_${PN} += "${libdir}/dri ${libdir}/gstreamer-0.10 ${libdir}/xorg/modules/drivers"
FILES_${PN}-dbg += "${libdir}/xorg/modules/drivers/.debug ${libdir}/dri/.debug ${libdir}/gstreamer-0.10/.debug"

S = "${WORKDIR}/${EMGD_RPM_DIR}"

do_install () {
    # A gstreamer VA buffer library
    rpm2cpio ${S}/${EMGD_VIDEO_PLUGIN_DIR}/gst-vabuffer*.rpm | cpio -id

    # MIX Common contains common classes, datatype, header files used by other MIX components
    rpm2cpio ${S}/${EMGD_VIDEO_PLUGIN_DIR}/mixcommon*.rpm | cpio -id

    # MIX Video Bitstream Parser is an user library interface for various video format bitstream parsing
    rpm2cpio ${S}/${EMGD_VIDEO_PLUGIN_DIR}/mixvbp*.rpm | cpio -id

    # MIX Video is an user library interface for various video codecs available on the platform.
    rpm2cpio ${S}/${EMGD_VIDEO_PLUGIN_DIR}/mixvideo*.rpm | cpio -id

    install -d -m 0755                                    ${D}${libdir}/gstreamer-0.10
    install -m 0755 ${S}/usr/lib/*                        ${D}${libdir}/

    # A gstreamer plugin that uses MIX Video for hardware accelerated video decoding and rendering.
    rpm2cpio ${S}/${EMGD_VIDEO_PLUGIN_DIR}/gst-plugins-mixvideo*.rpm | cpio -id

    # A collection of gstreamer plugins that uses VA libraries for hardware accelerated video rendering and text overlay.
    rpm2cpio ${S}/${EMGD_VIDEO_PLUGIN_DIR}/gst-plugins-va*.rpm | cpio -id

    install -m 0755 ${S}/usr/lib/gstreamer-0.10/*         ${D}${libdir}/gstreamer-0.10/

    # EMGD runtime graphics libraries
    rpm2cpio ${S}/emgd-bin*.rpm | xz -d | cpio -id

    install -d -m 0755                                    ${D}${libdir}/dri
    install -d -m 0755                                    ${D}${libdir}/xorg/modules/drivers
    install -d -m 0755                                    ${D}${sysconfdir}
    install -d -m 0755                                    ${D}${mandir}/man4
    install -m 0755 ${S}/usr/lib/*.so.*                   ${D}${libdir}/
    install -m 0755 ${S}/usr/lib/dri/*                    ${D}${libdir}/dri/
    install -m 0755 ${S}/usr/lib/xorg/modules/drivers/*   ${D}${libdir}/xorg/modules/drivers/
    install -m 0755 ${S}/etc/*                            ${D}${sysconfdir}/
    install -m 0755 ${S}/usr/share/man/man4/*             ${D}${mandir}/man4/

    # Khronos development headers needed for EGL, OpenGL-ES, and OpenVG development
    rpm2cpio ${S}/emgd-devel*.rpm | xz -d | cpio -id

    install -d -m 0755                                    ${D}${includedir}/EGL
    install -m 0755 ${S}/usr/include/EGL/*.h              ${D}${includedir}/EGL/
    install -d -m 0755                                    ${D}${includedir}/GLES
    install -m 0755 ${S}/usr/include/GLES/*.h             ${D}${includedir}/GLES/
    install -d -m 0755                                    ${D}${includedir}/GLES2
    install -m 0755 ${S}/usr/include/GLES2/*.h            ${D}${includedir}/GLES2/
    install -d -m 0755                                    ${D}${includedir}/KHR
    install -m 0755 ${S}/usr/include/KHR/*.h              ${D}${includedir}/KHR/
    install -d -m 0755                                    ${D}${includedir}/VG
    install -m 0755 ${S}/usr/include/VG/*.h               ${D}${includedir}/VG/

    ln -sf libEGL.so.1                                    ${D}${libdir}/libEGL.so
    ln -sf libGLES_CM.so.1                                ${D}${libdir}/libGLES_CM.so
    ln -sf libGLESv2.so.2                                 ${D}${libdir}/libGLESv2.so
    ln -sf libOpenVG.so.1                                 ${D}${libdir}/libOpenVG.so
    ln -sf libOpenVGU.so.1                                ${D}${libdir}/libOpenVGU.so
}

LEAD_SONAME = "libEGL.so"
