/*
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hbase.stargate.model;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.hadoop.hbase.util.Bytes;

import junit.framework.TestCase;

public class TestRowModel extends TestCase {

  static final byte[] ROW1 = Bytes.toBytes("testrow1");
  static final byte[] COLUMN1 = Bytes.toBytes("testcolumn1");
  static final byte[] VALUE1 = Bytes.toBytes("testvalue1");
  static final long TIMESTAMP1 = 1245219839331L;

  static final String AS_XML =
    "<Row key=\"dGVzdHJvdzE=\">" + 
      "<Cell timestamp=\"1245219839331\" column=\"dGVzdGNvbHVtbjE=\">" + 
        "dGVzdHZhbHVlMQ==</Cell>" + 
      "</Row>";

  JAXBContext context;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = JAXBContext.newInstance(
      CellModel.class,
      RowModel.class);
  }

  RowModel buildTestModel() {
    RowModel model = new RowModel();
    model.setKey(ROW1);
    model.addCell(new CellModel(COLUMN1, TIMESTAMP1, VALUE1));
    return model;
  }

  String toXML(RowModel model) throws JAXBException {
    StringWriter writer = new StringWriter();
    context.createMarshaller().marshal(model, writer);
    return writer.toString();
  }

  RowModel fromXML(String xml) throws JAXBException {
    return (RowModel)
      context.createUnmarshaller().unmarshal(new StringReader(xml));
  }

  void checkModel(RowModel model) {
    assertTrue(Bytes.equals(ROW1, model.getKey()));
    Iterator<CellModel> cells = model.getCells().iterator();
    CellModel cell = cells.next();
    assertTrue(Bytes.equals(COLUMN1, cell.getColumn()));
    assertTrue(Bytes.equals(VALUE1, cell.getValue()));
    assertTrue(cell.hasUserTimestamp());
    assertEquals(cell.getTimestamp(), TIMESTAMP1);
    assertFalse(cells.hasNext());
  }

  public void testRowModel() throws Exception {
    checkModel(buildTestModel());
    checkModel(fromXML(AS_XML));
  }
}
