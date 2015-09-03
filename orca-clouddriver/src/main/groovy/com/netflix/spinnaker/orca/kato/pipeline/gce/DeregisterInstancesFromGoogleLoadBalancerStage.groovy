/*
 * Copyright 2015 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.kato.pipeline.gce

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.orca.clouddriver.tasks.MonitorKatoTask
import com.netflix.spinnaker.orca.kato.tasks.WaitForDownInstanceHealthTask
import com.netflix.spinnaker.orca.kato.tasks.gce.DeregisterInstancesFromGoogleLoadBalancerTask
import com.netflix.spinnaker.orca.clouddriver.OortService
import com.netflix.spinnaker.orca.pipeline.LinearStage
import com.netflix.spinnaker.orca.pipeline.model.Stage
import groovy.transform.CompileStatic
import org.springframework.batch.core.Step
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@CompileStatic
class DeregisterInstancesFromGoogleLoadBalancerStage extends LinearStage {
  public static final String PIPELINE_CONFIG_TYPE = "deregisterInstancesFromLoadBalancer_gce"

  @Autowired
  OortService oortService

  @Autowired
  ObjectMapper objectMapper

  DeregisterInstancesFromGoogleLoadBalancerStage() {
    super(PIPELINE_CONFIG_TYPE)
  }

  @Override
  public List<Step> buildSteps(Stage stage) {
    def step1 = buildStep(stage, "deregisterInstances", DeregisterInstancesFromGoogleLoadBalancerTask)
    def step2 = buildStep(stage, "monitorInstances", MonitorKatoTask)
    def step3 = buildStep(stage, "waitForLoadBalancerState", WaitForDownInstanceHealthTask)
    [step1, step2, step3]
  }
}
