/**
 * Copyright (C) 2014 Open Whisper Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecure.internal.util;

import org.whispersystems.textsecure.api.util.TransferObserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Util {

  public static byte[][] split(byte[] input, int firstLength, int secondLength) {
    byte[][] parts = new byte[2][];

    parts[0] = new byte[firstLength];
    System.arraycopy(input, 0, parts[0], 0, firstLength);

    parts[1] = new byte[secondLength];
    System.arraycopy(input, firstLength, parts[1], 0, secondLength);

    return parts;
  }

  public static boolean isEmpty(String value) {
    return value == null || value.trim().length() == 0;
  }

  public static byte[] getSecretBytes(int size) {
    try {
      byte[] secret = new byte[size];
      SecureRandom.getInstance("SHA1PRNG").nextBytes(secret);
      return secret;
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  public static String readFully(InputStream in) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    byte[] buffer              = new byte[4096];
    int read;

    while ((read = in.read(buffer)) != -1) {
      bout.write(buffer, 0, read);
    }

    in.close();

    return new String(bout.toByteArray());
  }

  public static void readFully(InputStream in, byte[] buffer) throws IOException {
    int offset = 0;

    for (;;) {
      int read = in.read(buffer, offset, buffer.length - offset);

      if (read + offset < buffer.length) offset += read;
      else                		           return;
    }
  }


  public static void copy(InputStream in, OutputStream out, long contentLength, TransferObserver observer)
      throws IOException
  {
    byte[] buffer = new byte[4096];
    int read;
    int total = 0;

    if (observer != null) observer.onUpdate(0, contentLength);

    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
      total += read;
      if (observer != null) observer.onUpdate(total, contentLength);
    }

    in.close();
    out.close();
  }

  public static void copy(InputStream in, OutputStream out) throws IOException {
    copy(in, out, -1, null);
  }
}
