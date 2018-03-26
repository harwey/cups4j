/*
 * Copyright (c) 2018 by Oliver Boehm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 23.03.2018 by oboehm (ob@oasd.de)
 */
package org.cups4j.operations.ipp;

import org.cups4j.CupsClient;

/**
 * The class IppCreateJobOperation represents  he 
 *
 * @author oboehm
 * @since 0.7.2 (23.03.2018)
 */
public class IppSendDocumentOperation extends IppPrintJobOperation {
    
    public IppSendDocumentOperation() {
        this(CupsClient.DEFAULT_PORT);
    }
    
    public IppSendDocumentOperation(int port) {
        super(port);
        this.operationID = 0x0006;
    }
    
}
