#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
println "Current pipeline job build id is '${pipeline_id}'"
def node_label = 'CCI && ansible-2.3'
def mastervertical = MASTERVERTICAL_SCALE_TEST.toString().toUpperCase()

// run mastervert test
stage('mastervertical_scale_test') {
	if (MASTERVERTICAL_SCALE_TEST) {
		currentBuild.result = "SUCCESS"
		node('CCI && US') {
			// get properties file
			 if (fileExists("mastervertical.properties")) {
                                println "Looks like mastervertical.properties file already exists, erasing it"
                                sh "rm mastervertical.properties"
                        }
                        // get properties file
                        //sh "wget http://file.rdu.redhat.com/~nelluri/pipeline/nodevertical.properties"
                        sh "wget ${MASTERVERTICAL_PROPERTY_FILE}"
                        sh "cat mastervertical.properties"
                        def mastervertical_properties = readProperties file: "mastervertical.properties"
                        def jump_host = mastervertical_properties['JUMP_HOST']
                        def user = mastervertical_properties['USER']
                        def tooling_inventory_path = mastervertical_properties['TOOLING_INVENTORY']
			def clear_results = mastervertical_properties['CLEAR_RESULTS']
			def move_results = mastervertical_properties['MOVE_RESULTS']
			def containerized = mastervertical_properties['CONTAINERIZED']
			def proxy_user = mastervertical_properties['PROXY_USER']
			def proxy_host = mastervertical_properties['PROXY_HOST']
			def projects = mastervertical_properties['PROJECTS']

                        // debug info
                        println "JUMP_HOST: '${jump_host}'"
                        println "USER: '${user}'"
                        println "TOOLING_INVENTORY_PATH: '${tooling_inventory_path}'"
			println "CLEAR_RESULTS: '${clear_results}'"
			println "MOVE_RESULTS: '${move_results}'"
			println "CONTAINERIZED: '${containerized}'"
			println "PROXY_USER: '${proxy_user}'"
			println "PROXY_HOST: '${proxy_host}'"
			println "PROJECTS: '${projects}'"

                        // Run mastervertical job
                        try {
                           mastervertical_build = build job: 'MASTERVERTICAL-SCALE-TEST',
                                parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
                                                [$class: 'StringParameterValue', name: 'JUMP_HOST', value: jump_host ],
                                                [$class: 'StringParameterValue', name: 'USER', value: user ],
                                                [$class: 'StringParameterValue', name: 'TOOLING_INVENTORY', value: tooling_inventory_path ],
                                                [$class: 'StringParameterValue', name: 'CLEAR_RESULTS', value: clear_results ],
                                                [$class: 'StringParameterValue', name: 'MOVE_RESULTS', value: move_results ],
                                                [$class: 'StringParameterValue', name: 'CONTAINERIZED', value: containerized ],
                                                [$class: 'StringParameterValue', name: 'PROXY_USER', value: proxy_user ],
                                                [$class: 'StringParameterValue', name: 'PROXY_HOST', value: proxy_host ],
                                                [$class: 'StringParameterValue', name: 'PROJECTS', value: projects ]]
                        } catch ( Exception e) {
                       	echo "MASTERVERTICAL-SCALE-TEST Job failed with the following error: "
                        echo "${e.getMessage()}"
			echo "Sending an email"
			mail(
      				to: 'nelluri@redhat.com',
      				subject: 'Mastervertical-scale-test job failed',
      				body: """\
					Encoutered an error while running the mastervertical-scale-test job: ${e.getMessage()}\n\n
					Jenkins job: ${env.BUILD_URL}
			""")
                        currentBuild.result = "FAILURE"
                        sh "exit 1"
                        }
			println "MASTERVERTICAL-SCALE-TEST build ${mastervertical_build.getNumber()} completed successfully"
		}
			println "Stage 3: MASTERVERTICAL-SCALE-TEST OF PIPELINE BUILD '${pipeline_id} COMPLETED"
	}
}
