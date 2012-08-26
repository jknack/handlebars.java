/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ParboiledAgent {

  public static final String PACKAGE = ParboiledAgent.class.getPackage()
      .getName();

  public static String INTERNAL_PATH = PACKAGE.replace(".", "/") + "/internal";

  public static void premain(final String args,
      final Instrumentation instrumentation) throws Exception {

    final String actionClass = INTERNAL_PATH + "/Action$";
    System.out.println("Action class pattern: " + actionClass);
    final String parserClass = INTERNAL_PATH + "/Parser$$";
    System.out.println("Parser class pattern: " + parserClass);
    instrumentation.addTransformer(new ClassFileTransformer() {
      @Override
      public byte[] transform(final ClassLoader loader, final String className,
          final Class<?> classBeingRedefined,
          final ProtectionDomain protectionDomain,
          final byte[] byteCode) throws IllegalClassFormatException {
        if (className.startsWith(actionClass)
            || className.startsWith(parserClass)) {
          write(className, byteCode);
        }
        return byteCode;
      }
    });
  }

  protected static void write(final String className, final byte[] byteCode) {
    String[] paths = className.split("/");
    String baseDir =
        System.getProperty("output.dir") + "/"
            + className.replace(paths[paths.length - 1], "");
    File output = new File(baseDir, paths[paths.length - 1] + ".class");
    System.out.println("Writing " + output);
    try {
      OutputStream stream = new FileOutputStream(output);
      stream.write(byteCode);
      stream.flush();
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
