/*
 *
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.identity.recovery.endpoint.Utils;

import org.testng.annotations.Test;
import org.wso2.carbon.identity.captcha.util.CaptchaConstants;
import org.wso2.carbon.identity.recovery.IdentityRecoveryClientException;
import org.wso2.carbon.identity.recovery.IdentityRecoveryException;
import org.wso2.carbon.identity.recovery.util.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.wso2.carbon.identity.recovery.IdentityRecoveryConstants.ErrorMessages.ERROR_CODE_PRE_UPDATE_PASSWORD_ACTION_FAILURE;

/**
 * Unit tests for RecoveryUtils.java
 */
public class RecoveryUtilsTest {

    @Test(description = "To test the getValidatedCaptchaConfigs method.")
    public void testGetValidatedCaptchaConfigs() throws IdentityRecoveryException {

        Path path = Paths.get("src/test/resources", "repository", "conf", "identity",
                CaptchaConstants.CAPTCHA_CONFIG_FILE_NAME);
        Properties sampleProperties = new Properties();

        if (Files.exists(path)) {
            try (Reader in = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
                sampleProperties.load(in);
            } catch (IOException e) {
                throw new IdentityRecoveryException("Unable to read captcha config file.", e);
            }
        }

        System.setProperty("carbon.home", "src/test/resources");
        Properties properties = RecoveryUtil.getValidatedCaptchaConfigs();
        assertEquals(properties, sampleProperties);
        assertEquals(properties.size(), sampleProperties.size());
    }

    @Test(description = "Test the password reset API error format.")
    public void testHandleClientExceptionWithDescription() {

        String description = "Invalid password format";
        String message = "invalid_value";
        Throwable cause = new Throwable(description);

        IdentityRecoveryClientException exception;
        try {
            exception = Utils.handleClientException(ERROR_CODE_PRE_UPDATE_PASSWORD_ACTION_FAILURE, description, message,
                    cause);
        } catch (IdentityRecoveryClientException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(exception);
        assertEquals(exception.getErrorCode(), ERROR_CODE_PRE_UPDATE_PASSWORD_ACTION_FAILURE.getCode());
        assertEquals(exception.getMessage(), message);
        assertEquals(exception.getDescription(), description);
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testHandleClientExceptionWithoutDescription() {

        String message = "Test message";
        Throwable cause = new Throwable("Cause of the error");

        IdentityRecoveryClientException exception;
        try {
            exception =
                    Utils.handleClientException(ERROR_CODE_PRE_UPDATE_PASSWORD_ACTION_FAILURE, null, message, cause);
        } catch (IdentityRecoveryClientException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(exception);
        assertEquals(exception.getErrorCode(), ERROR_CODE_PRE_UPDATE_PASSWORD_ACTION_FAILURE.getCode());
        assertEquals(exception.getMessage(), message);
        assertEquals(exception.getDescription(), ERROR_CODE_PRE_UPDATE_PASSWORD_ACTION_FAILURE.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
