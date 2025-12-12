package com.lxp.aplus.course.application.port.out;

public interface FilePathGenerator {

    String generate(Long courseId, String originalFileName);
}
