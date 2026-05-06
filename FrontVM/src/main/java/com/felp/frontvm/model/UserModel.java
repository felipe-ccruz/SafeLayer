package com.felp.frontvm.model;

public class UserModel {

    private Long id;
    private String name;
    private String email;
    private String plan;
    private String planLabel;
    private int maxVms;
    private boolean unlimited;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public String getPlanLabel() { return planLabel; }
    public void setPlanLabel(String planLabel) { this.planLabel = planLabel; }

    public int getMaxVms() { return maxVms; }
    public void setMaxVms(int maxVms) { this.maxVms = maxVms; }

    public boolean isUnlimited() { return unlimited; }
    public void setUnlimited(boolean unlimited) { this.unlimited = unlimited; }
}
