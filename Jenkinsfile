pipeline {
    agent any

   environment {
       ANDROID_HOME = '/Users/nishan/Library/Android/sdk'
       PATH = "/opt/homebrew/bin:/Users/nishan/Library/Android/sdk/platform-tools:/Users/nishan/Library/Android/sdk/emulator:/Users/nishan/.nvm/versions/node/v24.14.0/bin:${env.PATH}"
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
                   echo "Checking port 4723..."
                   lsof -i :4723 || echo "Port 4723 is free"
                   echo "Killing any existing Appium processes..."
                   pkill -f "appium" || true
                   sleep 3

                   echo "Starting fresh Appium server..."
                   nohup appium --address 127.0.0.1 --port 4723 \
                   --log /tmp/appium.log \
                   --log-level info \
                   > /tmp/appium.log 2>&1 &

                   echo "Waiting for Appium..."
                   for i in $(seq 1 12); do
                       sleep 5
                       STATUS=$(curl -s http://127.0.0.1:4723/status || echo "")
                       if echo "$STATUS" | grep -q "ready"; then
                           echo "Appium ready after $((i*5)) seconds"
                           exit 0
                       fi
                       echo "Attempt $i — waiting..."
                   done
                   echo "Appium log:"
                   cat /tmp/appium.log
                   exit 1
               '''
           }
       }

        stage('Run Tests') {
            steps {
                sh './gradlew test -Dexplicit.wait=30'
            }
        }

       stage('Generate Allure Report') {
           steps {
               sh '/opt/homebrew/bin/allure generate allure-results --clean -o allure-report'
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
                echo "Stopping Appium..."
                pkill -f "appium" || true
                echo "Stopping emulator..."
                adb emu kill || true
                echo "Cleanup complete"
            '''
        }
    }
}