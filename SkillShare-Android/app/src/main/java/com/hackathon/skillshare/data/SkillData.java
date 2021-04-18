package com.hackathon.skillshare.data;

public class SkillData {
    String skill_id, skill_image, skill_name;
    boolean isSkillSelected;

    public boolean isSkillSelected() {
        return isSkillSelected;
    }

    public void setSkillSelected(boolean skillSelected) {
        isSkillSelected = skillSelected;
    }

    public SkillData(String skill_id, String skill_image, String skill_name) {
        this.skill_id = skill_id;
        this.skill_image = skill_image;
        this.skill_name = skill_name;
    }


    public SkillData(){}

    public String getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(String skill_id) {
        this.skill_id = skill_id;
    }

    public String getSkill_image() {
        return skill_image;
    }

    public void setSkill_image(String skill_image) {
        this.skill_image = skill_image;
    }

    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }
}
