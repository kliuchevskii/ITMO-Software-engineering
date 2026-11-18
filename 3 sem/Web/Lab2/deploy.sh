#!/bin/sh
# Раскладывает файлы на helios в ~/httpd-root и подставляет логин в конфиг
mkdir -p ~/httpd-root/www ~/httpd-root/fcgi-bin ~/httpd-root/mutex-dir
cp static/* ~/httpd-root/www/
cp server.jar ~/httpd-root/fcgi-bin/
sed "s#/home/studs/sXXXXXX#$HOME#g" httpd-root/httpd.conf > ~/httpd-root/httpd.conf
