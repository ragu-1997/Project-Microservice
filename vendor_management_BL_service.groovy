node('master') {
    try {
        stage('Checkout') {
         cleanWs()
    	checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[cancelProcessOnExternalsFail: true, credentialsId: '585fa701-720a-4602-abe6-b977c8773a4c', depthOption: 'infinity', ignoreExternalsOption: true, local: '.', remote: 'https://192.168.1.200/svn/api/Products/Cloud BLM/Application/branches/Experimental branch/BackEnd Services- Unified Database/CloudBLM-VendorManagementService/VendorManagement-BusinessLogic']], quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
    	}
    	stage('NPM Install') {
    		sh 'npm install'
    	}
    	stage('Build') {
    		sh 'npm run prod'
    	}
    	stage('Publish') {
    
    		sh label: 'Copy dist/server.js', script: 'sudo cp -v ${WORKSPACE}/dist/server.js /opt/apps/VendorManagementService/BusinessLogic/server.js'
            sh label: 'Copy Node Modules', script: 'sudo cp -R ${WORKSPACE}/node_modules /opt/apps/VendorManagementService/BusinessLogic'
            sh label: 'Copy server.config.js', script: 'sudo cp -v ${WORKSPACE}/server.config.js /opt/apps/VendorManagementService/BusinessLogic/server.config.js'
            deleteDir()
    	}
        currentBuild.result = 'SUCCESS'
    } catch (Exception err) {
        currentBuild.result = 'FAILURE'
    }
    stage('Notify') {
        if(currentBuild.result == 'FAILURE') {
            mail bcc: '', body: "Hi,\n Jenkins have some issues in  ${JOB_NAME} build #${BUILD_NUMBER} \n\n Build URL = ${BUILD_URL} \n\n Job URL = ${JOB_URL}", cc: '', from: 'sysadmin@srinsofttech.com', replyTo: '', subject: "Problem in ${JOB_NAME} build #${BUILD_NUMBER}", to: 'jagadeesan@srinsofttech.com'
        }
    }		
}