binaryRepository {
	url = 'http://aus02ndmms001.dev.volusion.com:8081/nexus/content/repositories'
	username = 'deployment'
	password = 'deployment123'
	name = 'releases'
}


environments {
	uat {
		mozu {
			baseUrl = 'https://home.mozu.com'
			appId = 'afa8d0c.giftcard.1.0.0.release'
			sharedSecret = '1dcd82723cdf4aacb986b5a13ba4eba3'
			tenantId = '12020'
			dbQueue = 'sunandskigiftcardbackingstore'
			dbNotificationTime = 'sslastnotification'
			dbNamespace = 'afa8d0c'
		}

		server {
			hostname = 'host.mozu.com'
			port = 80
			context = 'sunandski'
			username = 'jenkins'
			password = 'jenkins'
		}
	}
}
