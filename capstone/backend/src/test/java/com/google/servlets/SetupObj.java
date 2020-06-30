package com.google.servlets;

import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class SetupObj {
  HttpServletRequest request;
  HttpServletResponse response;
  StringWriter writer;

  public SetupObj(HttpServletRequest request, HttpServletResponse response, 
      StringWriter writer) {
    this.request = request;
    this.response = response;
    this.writer = writer;
  }
}