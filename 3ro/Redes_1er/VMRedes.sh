#! /bin/bash

# Desactiva procesos de virtualizacion que chocan con la VM
sudo modprobe -r kvm_intel kvm
# Abre VirtualBox utilizando X11
QT_QPA_PLATFORM=xcb VirtualBox &
