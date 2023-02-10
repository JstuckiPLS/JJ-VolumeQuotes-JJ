/**
 * 
 */
package com.pls.core.domain.bo.dashboard;

import java.util.List;

/**
 * KPI report filters BO.
 * 
 * @author Alexander Nalapko
 * 
 */
public class KPIReportFiltersBO {

    private List<ScacBO> scac;
    private List<String> classCode;
    private List<String> orig;
    private List<String> dest;
    private List<String> ioFlag;
    private List<String> weekday;
    private List<String> year;
    private List<String> weight;
    private List<String> month;
    private List<String> status;

    public List<String> getMonth() {
        return month;
    }

    public void setMonth(List<String> month) {
        this.month = month;
    }

    public List<String> getYear() {
        return year;
    }

    public void setYear(List<String> year) {
        this.year = year;
    }

    public void setIoFlag(List<String> ioFlag) {
        this.ioFlag = ioFlag;
    }

    public List<String> getClassCode() {
        return classCode;
    }

    public void setClassCode(List<String> classCode) {
        this.classCode = classCode;
    }

    public List<String> getOrig() {
        return orig;
    }

    public void setOrig(List<String> orig) {
        this.orig = orig;
    }

    public List<String> getDest() {
        return dest;
    }

    public void setDest(List<String> dest) {
        this.dest = dest;
    }

    public List<String> getIoFlag() {
        return ioFlag;
    }

    public void setIOFlag(List<String> ioFlag) {
        this.ioFlag = ioFlag;
    }

    public List<String> getWeekday() {
        return weekday;
    }

    public void setWeekday(List<String> weekday) {
        this.weekday = weekday;
    }

    public List<String> getWeight() {
        return weight;
    }

    public void setWeight(List<String> weight) {
        this.weight = weight;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<ScacBO> getScac() {
        return scac;
    }

    public void setScac(List<ScacBO> scac) {
        this.scac = scac;
    }
}
