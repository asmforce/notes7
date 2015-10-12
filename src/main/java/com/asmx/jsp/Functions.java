package com.asmx.jsp;

import org.apache.commons.lang3.StringUtils;
import org.apache.taglibs.standard.tag.common.core.Util;

import javax.servlet.jsp.JspException;
import java.util.Random;

/**
 * User: asmforce
 * Timestamp: 05.08.15 0:32.
**/
public class Functions {
    private static final Random random = new Random(System.currentTimeMillis());
    private static final IdGenerator idGenerator = new IdGenerator(random);

    public static int randomInt() {
        return random.nextInt();
    }

    public static int randomInt(int bound) {
        return random.nextInt(bound);
    }

    public static long randomLong() {
        return random.nextLong();
    }

    public static String randomId(int length) throws JspException {
        return idGenerator.generate(length);
    }

    public static String escapeJs(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);

            switch (c) {
                case '\\':
                case '\'':
                case '\"':
                    builder.append('\\');
                    // Fall-through

                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }

    public static String escapeXml(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        return Util.escapeXml(code);
    }

    public static class IdGenerator {
        private Random random;
        private String characters = "0123456789abcdefghijklmnopqrstuvwxyz";

        public IdGenerator(Random random) {
            this.random = random;
        }

        public String generate(int length) throws JspException {
            if (length < 1) {
                throw new JspException("length < 1");
            }

            char[] chars = new char[length];
            for (int i = 0; i < length; i++) {
                chars[i] = characters.charAt(random.nextInt(characters.length()));
            }
            return new String(chars);
        }
    }
}
