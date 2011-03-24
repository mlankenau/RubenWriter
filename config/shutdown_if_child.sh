online=`ps ax | grep "/home/r[u]ben"`
if [ -n "$online" ]; then 
	/sbin/shutdown -h now
fi
