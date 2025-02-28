@Library("fdg_parent@release/5.0") _

node('java17-buildah') {

    container("java") {

        stageCheckout()

	    stageCompile()

	    stageTest()

	    stageIntegrationTest()

    }

}

void stageCheckout() {
	stage('checkout') {
        checkout scm
	}
}

void stageCompile() {
    stage('compile') {
        sh "mvn -U -B clean test-compile"
    }
}

void stageTest() {
    stage('test') {
        sh "mvn -U -B -Dmaven.test.failure.ignore=true -Dmaven.javadoc.skip=true test"
        junit '**/target/surefire-reports/**/TEST-*.xml'
    }
}

void stageIntegrationTest() {
    stage('integration test') {
        sh "mvn -U -B -Dmaven.test.failure.ignore=true -Dcups.url=http://cups.int.ad.drgueldener.de:12197 -Dprinter=OPTDN075 failsafe:integration-test failsafe:verify"
        junit '**/target/failsafe-reports/**/TEST-*.xml'
    }
}
