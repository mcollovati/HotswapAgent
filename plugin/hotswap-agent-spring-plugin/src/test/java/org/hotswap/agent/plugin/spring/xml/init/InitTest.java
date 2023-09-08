/*
 * Copyright 2013-2023 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.plugin.spring.xml.init;

import org.hotswap.agent.plugin.spring.BeanFactoryAssistant;
import org.hotswap.agent.plugin.spring.ReconfigureTestParam;
import org.hotswap.agent.util.test.WaitHelper;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InitTest {
    private static final Resource xmlFile1 = new ClassPathResource("initContext-1.xml");
    private static final Resource xmlFile2 = new ClassPathResource("initContext-2.xml");


    @Test
    public void swapXmlTest() throws Exception {
        ReconfigureTestParam.configMaxReloadTimes(2);
        AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:initContext-1.xml");
        assertEquals(1, FooBean.getStaticValue());


        Files.copy(xmlFile2.getFile().toPath(), xmlFile1.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
        assertTrue(WaitHelper.waitForCommand(new WaitHelper.Command() {
            @Override
            public boolean result() throws Exception {
                return BeanFactoryAssistant.getBeanFactoryAssistant(applicationContext.getBeanFactory()).getReloadTimes() >= 1;
            }
        }, 5000));

        assertEquals(2, FooBean.getStaticValue());
    }
}
