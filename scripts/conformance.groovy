#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
println "Current pipeline job build id is '${pipeline_id}'"
def node_label = 'CCI && ansible-2.3'

// stage 1: conformance test
stage ('conformance') {
	  if (CONFORMANCE) {
		currentBuild.result = "SUCCESS"
		node('CCI && US') {
			// get properties file
			if (fileExists("conformance.properties")) {
				println "Looks like conformance.properties file already exists, erasing it"
				sh "rm conformance.properties"
			}
			// get properties file
			//sh "wget http://file.rdu.redhat.com/~nelluri/pipeline/conformance.properties"
			sh "wget ${CONFORMANCE_PROPERTY_FILE}"
			sh "cat conformance.properties"
			def conformance_properties = readProperties file: "conformance.properties"
			def master_host = conformance_properties['MASTER_HOST']
			def user = conformance_properties['USER']
			def enable_pbench = conformance_properties['ENABLE_PBENCH']
		        def use_proxy = conformance_properties['USE_PROXY']
                        def proxy_user = conformance_properties['PROXY_USER']
                        def proxy_host = conformance_properties['PROXY_HOST']

                        // debug info
                        println "----------USER DEFINED OPTIONS-------------------"
                        println "-------------------------------------------------"
                        println "-------------------------------------------------"
                        println "JUMP_HOST: '${jump_host}'"
                        println "USER: '${user}'"
                        println "ENABLE_PBENCH: '${enable_pbench}'"
                        println "USE_PROXY: '${use_proxy}'"
                        println "PROXY_USER: '${proxy_user}'"
                        println "PROXY_HOST: '${proxy_host}'"
                        println "-------------------------------------------------"
                        println "-------------------------------------------------"	
		
			// Run conformance job
			try {
			    conformance_build = build job: 'SVT_conformance',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'MASTER_HOSTNAME', value: master_host ],
						[$class: 'StringParameterValue', name: 'MASTER_USER', value: user ],
						[$class: 'StringParameterValue', name: 'ENABLE_PBENCH', value: enable_pbench ],
						[$class: 'StringParameterValue', name: 'USE_PROXY', value: use_proxy ],
						[$class: 'StringParameterValue', name: 'PROXY_USER', value: proxy_user ],
						[$class: 'StringParameterValue', name: 'PROXY_HOST', value: proxy_host ]]
			} catch ( Exception e) {
                	echo "CONFORMANCE Job failed with the following error: "
                	echo "${e.getMessage()}"
                	currentBuild.result = "FAILURE"
			sh "exit 1"
            		}
                	println "CONFORMANCE build ${conformance_build.getNumber()} completed successfully"
		}
	}
			println "Stage 1: CONFORMANCE OF PIPELINE BUILD '${pipeline_id} COMPLETED"
}
