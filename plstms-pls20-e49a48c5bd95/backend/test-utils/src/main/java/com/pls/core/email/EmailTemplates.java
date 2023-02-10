package com.pls.core.email;

/**
 * Class that contains email templates for tests.
 * 
 * @author Aleksandr Leshchenko
 */
public final class EmailTemplates {
    public static final String EMAIL_HEADER =
            "<html>\n"
          + "    <head>\n"
          + "        <title></title>\n"
          + "        <link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.plspro.com/email/emailStylesheetHTML.css\"> </link>\n"
          + "        <style>\n"
          + "            td.noPadding {\n"
          + "                padding: 0 !important;\n"
          + "            }\n"
          + "            td { padding: 0px 25px; }\n"
          + "            #footer p\n"
          + "            {\n"
          + "                margin: 0;\n"
          + "            }\n"
          + "        </style>\n"
          + "    </head>\n"
          + "    <body>\n"
          + "        <div id=\"header\">\n"
          + "        </div>\n"
          + "        <div id=\"main\">";
    public static final String EMAIL_FOOTER =
            "        </div>\n"
          + "        <br/><br/>\n"
          + "        <div id=\"footer\">\n"
          + "            <div>\n"
          + "                <p>PLS Contact: LTL Customer Service</p>\n"
          + "                <p>Email: <a href=\"mailto:plsfreight1@plslogistics.com\">plsfreight1@plslogistics.com</a></p>\n"
          + "                <p>Phone: 1-888-757-8261</p>\n"
          + "                <p>Thank you for using PLS Logistics</p>\n"
          + "            </div>\n"
          + "            <div id=\"logo\" class=\"left\"></div>\n"
          + "            <div id=\"printLogo\" class=\"left\">\n"
          + "                <img align=\"center\" width=\"100\" height=\"40\" src=\"http://www.plspro.com/email/pls_logo_new.jpg\"/>\n"
          + "            </div>\n"
          + "            <div id=\"headerImage\" class=\"right\"></div>\n"
          + "        </div>\n"
          + "    </body>\n"
          + "</html>";

    private EmailTemplates() {
    }
}
