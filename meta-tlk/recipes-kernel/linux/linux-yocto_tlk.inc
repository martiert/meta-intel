FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = " file://time-limited-kernel.cfg"
PRINC := "${@int(PRINC) + 1}"
