package wiki.zyue.eou.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import javax.validation.constraints.Size;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * 2021/12/28 04:46:46
 * @author echo
 */
@MappedSuperclass
@SuppressWarnings({"unchecked", "unused"})
public class BaseEntity<E extends BaseEntity<E>> {

  /**
   * id 主键
   */
  @Id
  private String id;

  /**
   * 名称
   */
  @Size(max = 255, message = "name 长度不能超过 30")
  private String name;

  /**
   * 全称
   */
  @Size(max = 255, message = "spell 长度不能超过 55")
  private String spell;

  /**
   * 排序
   */
  private Integer order;

  /**
   * 创建时间
   */
  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /**
   * 创建用户
   */
  @CreatedBy
  private String createUser;

  /**
   * 修改时间
   */
  @LastModifiedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime modifyTime;

  /**
   * 修改用户
   */
  @LastModifiedBy
  private String modifyUser;

  /**
   * 备注
   */
  @Size(max = 255, message = "remark 长度不能超过 255")
  private String remark;

  /**
   * 是否启用
   */
  private Boolean isEnable = true;

  public String getId() {
    return id;
  }

  public E setId(String id) {
    this.id = id;
    return (E) this;
  }

  public String getName() {
    return name;
  }

  public E setName(String name) {
    this.name = name;
    return (E) this;
  }

  public String getSpell() {
    return spell;
  }

  public E setSpell(String spell) {
    this.spell = spell;
    return (E) this;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public E setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return (E) this;
  }

  public String getCreateUser() {
    return createUser;
  }

  public E setCreateUser(String createUser) {
    this.createUser = createUser;
    return (E) this;
  }

  public LocalDateTime getModifyTime() {
    return modifyTime;
  }

  public E setModifyTime(LocalDateTime modifyTime) {
    this.modifyTime = modifyTime;
    return (E) this;
  }

  public String getModifyUser() {
    return modifyUser;
  }

  public E setModifyUser(String modifyUser) {
    this.modifyUser = modifyUser;
    return (E) this;
  }

  public String getRemark() {
    return remark;
  }

  public E setRemark(String remark) {
    this.remark = remark;
    return (E) this;
  }

  public Boolean getEnable() {
    return isEnable;
  }

  public E setEnable(Boolean enable) {
    this.isEnable = enable;
    return (E) this;
  }

  public Integer getOrder() {
    return order;
  }

  public E setOrder(Integer order) {
    this.order = order;
    return (E) this;
  }


  @Override
  public String toString() {
    return "BaseEntity{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", spell='" + spell + '\'' +
        ", order=" + order +
        ", createTime=" + createTime +
        ", createUser='" + createUser + '\'' +
        ", modifyTime=" + modifyTime +
        ", modifyUser='" + modifyUser + '\'' +
        ", remark='" + remark + '\'' +
        ", isEnable=" + isEnable +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseEntity<?> that)) {
      return false;
    }
    return Objects.equals(getId(), that.getId()) && Objects.equals(getName(),
        that.getName()) && Objects.equals(getSpell(), that.getSpell())
        && Objects.equals(getOrder(), that.getOrder()) && Objects.equals(
        getCreateTime(), that.getCreateTime()) && Objects.equals(getCreateUser(),
        that.getCreateUser()) && Objects.equals(getModifyTime(), that.getModifyTime())
        && Objects.equals(getModifyUser(), that.getModifyUser())
        && Objects.equals(getRemark(), that.getRemark()) && Objects.equals(
        isEnable, that.isEnable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getSpell(), getOrder(), getCreateTime(),
        getCreateUser(),
        getModifyTime(), getModifyUser(), getRemark(), isEnable);
  }
}
