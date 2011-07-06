package com.talend.tac.cases.grid;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.Parameters;


public class TestGridExecutionsAgainGenerating extends Grid {

	@Test
	@Parameters({"grid.task.label","labelDescription","AddcommonProjectname","branchNameTrunk","jobNameTJava","version0.1",
		"context","ServerForUseAvailable","statisticEnabled"})
	public void testTaskRegenerating(String label, String description, String projectName, String branchName, String jobName, String version, String context, String serverName, String statisticName) {
		this.cleanTask();
		this.addTask(label, description, projectName, branchName, jobName, version, context, serverName, statisticName);
	    selenium.mouseDown("//span[text()='" + label + "']");
	    selenium.click("//div[text()='Job Conductor']//ancestor::div[@class='x-panel-body x-panel-body-noheader x-panel-body-noborder x-border-layout-ct']//button[@id='idJobConductorTaskGenerateButton']");
	    this.waitForTextPresent("Generating...", WAIT_TIME);
	    this.waitForElementPresent("//span[text()='Ready to deploy']", WAIT_TIME);
	    selenium.click("//div[text()='Job Conductor']//ancestor::div[@class='x-panel-body x-panel-body-noheader x-panel-body-noborder x-border-layout-ct']//button[@id='idJobConductorTaskDeployButton']");
	    Assert.assertFalse(this.waitElement("//span[text()='Generating...']", 15), "test failed because task was regenerated!");
	    this.waitForElementPresent("//span[text()='Ready to run']", WAIT_TIME);
	    this.deleteTask(label);
	}
	
}