require 'logger'

class InetServer
	SIGNAL_FILE = '/tmp/inet_on'

	def initialize
		@log = Logger.new("/tmp/inet_server.log")
		@inet_status = :up
		@signaled = false
		File.delete SIGNAL_FILE if File.file?(SIGNAL_FILE) 
	end

	def log(s)
		@log.info(s)
	end

	def change_inet (new_status)
		if (new_status == :down && @inet_status == :up)
			log("stopping inet")
			`/sbin/iptables -A OUTPUT -j REJECT`
		end
		if (new_status == :up && @inet_status == :down)
			log("starting inet")
			`/sbin/iptables -D OUTPUT 1`
		end

		@inet_status = new_status
	end


	def user_online
		regex = Regexp.new("/usr/lib/gvfs//gvfs-fuse-daemon /home/([a-z]+)/.gvfs")
		regex.match(`ps ax | grep [g]vfs`)
		$1
	end

	def user_changed(user)
		log("user changed to #{user}")
		if (user == "ruben")
			change_inet(:down)	
		else 
			change_inet(:up)
		end
	end

	def run
		last_user = user_online
		log("server startet")
		while true
			sleep 1.0
			begin
				user = user_online
				if (user != last_user)
					user_changed user
					last_user = user;
				end


				if (@signaled && !File.file?(SIGNAL_FILE))
					log("signal file disappeared")
					change_inet(:down)
					@signaled = false
				end

				if (File.file?(SIGNAL_FILE) && @inet_status == :down) 
					log("signal file appeared")
					change_inet(:up)
					@signaled = true
				end

			rescue e
				log("error: #{e}")
			end
		end
	end
end

InetServer.new.run
