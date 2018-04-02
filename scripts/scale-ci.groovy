#!/usr/bin/env groovy

def pipeline_id = env.BUILD_ID
println "Current pipeline job build id is '${pipeline_id}'"
def node_label = 'CCI && ansible-2.3'

// install openshift
stage ('openshift_install') {
	if (OPENSHIFT_INSTALL) {
		currentBuild.result = "SUCCESS"
		node('CCI && US') {
			// get properties file
			if (fileExists("openshift.properties")) {
				println "Looks like openshift.properties file already exists, erasing it"
				sh "rm openshift.properties"
			}
			// get properties file
			//sh  "wget http://file.rdu.redhat.com/~nelluri/pipeline/openshift.properties"
			sh "wget ${OPENSHIFT_PROPERTY_FILE}"
			sh "cat openshift.properties"
			def openshift_properties = readProperties file: "openshift.properties"
			def openstack_server = openshift_properties['OPENSTACK_SERVER']
			def openstack_user = openshift_properties['OPENSTACK_USER']
			def image_server = openshift_properties['IMAGE_SERVER']
			def image_user = openshift_properties['IMAGE_USER']
			def branch = openshift_properties['BRANCH']
			def openshift_node_flavor = openshift_properties['OPENSHIFT_NODE_FLAVOR']
			def openshift_registries = openshift_properties['OPENSHIFT_REGISTRIES']
			def time_servers = openshift_properties['TIME_SERVERS']
			def jenkins_slave_label = openshift_properties['JENKINS_SLAVE_LABEL']

                        // debug info
                        println "----------USER DEFINED OPTIONS-------------------"
                        println "-------------------------------------------------"
                        println "-------------------------------------------------"
                        println "OPENSHIFT_SERVER: '${openshift_server}'"
                        println "OPENSTACK_SERVER: '${openstack_server}'"
                        println "OPENSTACK_USER: '${openstack_user}'"
                        println "IMAGE_SERVER: '${image_server}'"
                        println "IMAGE_USER: '${image_user}'"
                        println "BRANCH: '${branch}'"
			println "OPENSHIFT_NODE_FLAVOR: '${openshift_node_flavor}'"
			println "OPENSHIFT_REGISTRIES: '${openshift_registries}'"
			println "TIME_SERVERS: '${time_servers}'"
			println "JENKINS_SLAVE_LABEL: '${jenkins_slave_label}'"
                        println "-------------------------------------------------"
                        println "-------------------------------------------------"	
		
			// Run scale-ci openshift install
			try {
			    openshift_build = build job: 'scale-ci_install_OpenShift',
				parameters: [   [$class: 'LabelParameterValue', name: 'node', label: node_label ],
						[$class: 'StringParameterValue', name: 'OPENSHIFT_SERVER', value: openshift_server ],
						[$class: 'StringParameterValue', name: 'OPENSTACK_SERVER', value: openstack_server ],
						[$class: 'StringParameterValue', name: 'OPENSTACK_USER', value: openstack_user ],
						[$class: 'StringParameterValue', name: 'IMAGE_SERVER', value: image_server ],
						[$class: 'StringParameterValue', name: 'IMAGE_USER', value: image_user ],
						[$class: 'StringParameterValue', name: 'branch', value: branch ],
						[$class: 'StringParameterValue', name: 'openshift_node_flavor', value: openshift_node_flavor ],
                                                [$class: 'StringParameterValue', name: 'openshift_registries', value: openshift_registries ],
                                                [$class: 'StringParameterValue', name: 'time_servers', value: time-servers ],
                                                [$class: 'StringParameterValue', name: 'JENKINS_SLAVE_LABEL', value: jenkins_slave_label ]]
			} catch ( Exception e) {
                	echo "SCALE_CI_OPENSHIFT_INSTALL Job failed with the following error: "
                	echo "${e.getMessage()}"
                	currentBuild.result = "FAILURE"
			sh "exit 1"
            		}
                	println "SCALE-CI-OPENSHIFT-INSTALL build ${openshift_build.getNumber()} completed successfully"
		}
	}
			println "Stage 1: SCALE-CI-OPENSHIFT-INSTALL OF PIPELINE BUILD '${pipeline_id} COMPLETED"
}
