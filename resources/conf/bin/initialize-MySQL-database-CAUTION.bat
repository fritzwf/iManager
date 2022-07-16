@echo off
echo .
echo Cleaning the iManager database.  All records will be lost!!!!
echo .
echo If this is not what you wanted to do, hit CTRL-C to cancel this action.
echo .
pause
mysql -uroot -proot -e"drop database if exists imanager"
mysql -uroot -proot -e"create database if not exists imanager"
echo .
echo .
echo The iManager database has been initialized.
echo .
pause