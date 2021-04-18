#!groovy

pipeline {
    agent { node { label 'linux' } }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
    }

    tools {
        maven 'M3'
        jdk 'Java 8'
    }

    environment {
        NEXUS_CRED = credentials('Nexus')
        GRADLE_NEXUS_CREDS = "-q -PnexusUser=${env.NEXUS_CRED_USR} -PnexusPassword=${env.NEXUS_CRED_PSW}"
        DOCKER_HUB = 'hub.docker.com'
        DOCKER_TASK_CONSUMER_IMAGE = "${env.DOCKER_HUB}/cqrs-example/task-consumer-spring"
        DOCKER_TASK_API_IMAGE = "${env.DOCKER_HUB}/cqrs-example/task-api-spring"
    }

    stages {
        stage('Compile') {
            steps {
                echo 'Compile messaging'
                sh "./gradlew :messaging:compileJava :messaging:compileTestJava"

                echo 'Compile commandbus'
                sh "./gradlew :commandbus:compileJava :commandbus:compileTestJava"

                echo 'Compile eventbus'
                sh "./gradlew :eventbus:compileJava :eventbus:compileTestJava"

                echo 'Compile eventstore'
                sh "./gradlew :eventstore:compileJava :eventstore:compileTestJava"

                echo 'Compile domain'
                sh "./gradlew :domain:compileJava :domain:compileTestJava"

                echo 'Compile example task manager'
                sh """
                    ./gradlew :examples:task-manager:task-events:compileJava :examples:task-manager:task-events:compileTestJava \
                        :examples:task-manager:task-domain:compileJava :examples:task-manager:task-domain:compileTestJava \
                        :examples:task-manager:task-api-spring:compileJava :examples:task-manager:task-api-spring:compileTestJava \
                        :examples:task-manager:task-consumer-spring:compileJava :examples:task-manager:task-consumer-spring:compileTestJava
                """
            }
        }

       /* stage('Library Test') {
            steps {
                echo "Boot dependencies for integration test"
                sh "docker-compose -p integration-test up -d"
                sleep 15

                echo 'Clean previous tests'
                sh "./gradlew cleanTest"

                echo 'Run messaging tests'
                sh "./gradlew :messaging:test"

                echo 'Run commandbus tests'
                sh "./gradlew :commandbus:test"

                echo 'Run eventbus tests'
                sh "./gradlew :eventbus:test"

                echo 'Run eventstore tests'
                sh "./gradlew :eventstore:test"

                echo 'Run domain tests'
                sh "./gradlew :domain:test"

                echo "Shut down dependencies for integration test"
                sh "docker-compose -p integration-test down"
            }
        }

        stage('Example Test') {
            steps {
                echo "Boot dependencies for example"
                sh """
                    docker-compose -f examples/task-manager/docker-compose.yml -p task-mgr-test up -d \
                        zookeeper kafka db-task-manager db-eventstore
                """
                sleep 15

                echo 'Run TaskManager domain tests'
                sh "./gradlew :examples:task-manager:task-domain:test"

                echo 'Run TaskManager spring consumer tests'
                sh "./gradlew :examples:task-manager:task-consumer-spring:test"

                echo 'Run TaskManager spring api tests'
                sh "./gradlew :examples:task-manager:task-api-spring:test"

                echo "Shut down dependencies for example"
                sh "docker-compose -f examples/task-manager/docker-compose.yml -p task-mgr-test down"

                echo 'Generating test reports'
                sh "./gradlew jacocoTestReport"
            }
        }

        stage('Sonarqube') {
            when {
                expression { BRANCH_NAME ==~ /(develop|master|.*quality.*|release\/.*|^PR-\d+)/ }
            }
            steps {
                echo 'Analyzing code'
                sh "docker-compose -p integration-test up -d"
                sleep 15
                withSonarQubeEnv('My SonarQube Server') {
                    sh """
                        ./gradlew --info sonarqube
                        docker-compose -p integration-test down
                    """
                }
              //  timeout(time: 5, unit: 'MINUTES') {
                //    script {
                  //      def qg = waitForQualityGate()
                    //    if (qg.status == 'WARN') {
                      //      currentBuild.result = 'UNSTABLE'
                       // } else if (qg.status != 'OK') {
                         //   error "Pipeline aborted due to quality gate failure: ${qg.status}"
                       // }
                    //}
               // }
            }
        }
        
        */

        stage('Create artifacts') {
            when {
                expression { BRANCH_NAME ==~ /(develop|master)/ }
            }
            parallel {
                stage('messaging') {
                    steps {
                        echo 'Package messaging'
                        sh './gradlew :messaging:jar'
                    }
                }
                stage('commandbus') {
                    steps {
                        echo 'Package commandbus'
                        sh './gradlew :commandbus:jar'
                    }
                }
                stage('eventbus') {
                    steps {
                        echo 'Package eventbus'
                        sh './gradlew :eventbus:jar'
                    }
                }
                stage('eventstore') {
                    steps {
                        echo 'Package eventstore'
                        sh './gradlew :eventstore:jar'
                    }
                }
                stage('domain') {
                    steps {
                        echo 'Package domain'
                        sh './gradlew :domain:jar'
                    }
                }
                stage('example-task-manager') {
                    steps {
                        echo 'Create task manager boot JAR artifacts...'
                        sh './gradlew -PnexusUrl=https://hub.docker.com :examples:task-manager:task-consumer-spring:bootJar :examples:task-manager:task-api-spring:bootJar --stacktrace' 
                             
                        
                        echo 'Creating Docker image for api and projections'
                sh """
                    ./devtools/build-docker-image.sh examples/task-manager/task-api-spring "${DOCKER_TASK_API_IMAGE}" 
                    ./devtools/build-docker-image.sh examples/task-manager/task-consumer-spring "${DOCKER_TASK_CONSUMER_IMAGE}"
                """
                        
                        
                    }
                }
            }
        }

        stage('Publish artifacts') {
            when {
                expression { BRANCH_NAME ==~ /(develop|master)/ }
            }
            steps {
             //   echo 'Publishing artifacts...'
             //   sh "./gradlew ${GRADLE_NEXUS_CREDS} publish -x :jenkins:publish"
                
                echo 'Publishing java spring boot JAR modules to nexus...'
                sh "./gradlew -PnexusUrl=https://hub.docker.com ${GRADLE_NEXUS_CREDS} publish"

                echo 'Publishing docker images...'
                sh """
                    docker push ${DOCKER_TASK_CONSUMER_IMAGE}
                    docker push ${DOCKER_TASK_API_IMAGE}
                """
            }
        }
    }

    post {
        always {
            script {
                if (BRANCH_NAME.startsWith('PR')) {
                    env.channel = '#vnext-pull-requests'
                } else {
                    env.channel = '#vnext-ci'
                }
            }

        //    sh '''
        //        docker-compose -p integration-test kill
        //        docker-compose -f examples/task-manager/docker-compose.yml -p task-mgr-test kill
        //    '''

           // junit '**/build/test-results/junit-platform/*.xml'

           /* publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'commandbus/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "Commandbus coverage report"
            ]

            publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'domain/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "Domain coverage report"
            ]

            publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'eventbus/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "Eventbus coverage report"
            ]

            publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'eventstore/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "Eventstore coverage report"
            ]

            publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'messaging/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "Messaging coverage report"
            ]

            publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'examples/task-manager/task-domain/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "TaskManager Domain coverage report"
            ]

            publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'examples/task-manager/task-api-spring/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "TaskManager Spring api coverage report"
            ]

            publishHTML target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: false,
                keepAll              : true,
                reportDir            : 'examples/task-manager/task-consumer-spring/build/reports/jacoco/test/html/',
                reportFiles          : 'index.html',
                reportName           : "TaskManager Spring consumer coverage report"
            ]
			*/
        }

        unstable {
            echo 'Warning!'
            slackSend channel: env.channel, color: "warning", message: "Build unstable: <${env.JOB_DISPLAY_URL}|${env.JOB_NAME}> <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}>"
        }

        failure {
            echo 'Failure!'
            slackSend channel: env.channel, color: "danger", message: "Build Failed: <${env.JOB_DISPLAY_URL}|${env.JOB_NAME}> <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}>"
        }

        success {
            echo 'Success!'
            slackSend channel: env.channel, color: "good", message: "Build Passed: <${env.JOB_DISPLAY_URL}|${env.JOB_NAME}> <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}>"
        }
    } 
}
