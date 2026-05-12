pipeline {
    agent any

    environment {
        ANDROID_HOME = '/Users/nishan/Library/Android/sdk'
        PATH = "/Users/nishan/Library/Android/sdk/platform-tools:/Users/nishan/Library/Android/sdk/emulator:/Users/nishan/.nvm/versions/node/v24.14.0/bin:${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/msaninishan/mobile-automation-framework.git',
                    credentialsId: 'github-credentials'
            }
        }

       stage('Start Emulator') {
           steps {
               sh '''
                   echo "Starting Android emulator..."
                   nohup emulator -avd Pixel_10_Pro -no-window -no-audio > /tmp/emulator.log 2>&1 &
                   echo "Waiting for device..."
                   adb wait-for-device
                   sleep 30
                   echo "Emulator ready"
               '''
           }
       }

        stage('Start Appium Server') {
            steps {
                sh '''
                    echo "Starting Appium server..."
                    appium --address 127.0.0.1 --port 4723 &
                    sleep 5
                    echo "Appium server started"
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Generate Allure Report') {
            steps {
                sh 'allure generate allure-results --clean -o allure-report'
            }
        }
    }

    post {
        always {
               allure([
                   includeProperties: false,
                   jdk: '',
                   commandline: 'AllureCLI',
                   results: [[path: 'allure-results']]
               ])
           }

        success {
            echo 'Pipeline completed successfully'
        }

        failure {
            echo 'Pipeline failed — check Allure report'
        }

        cleanup {
            sh '''
                echo "Stopping Appium server..."
                pkill -f "appium" || true
                echo "Stopping emulator..."
                adb emu kill || true
            '''
        }
    }
}