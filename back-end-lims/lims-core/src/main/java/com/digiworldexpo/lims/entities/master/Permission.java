package com.digiworldexpo.lims.entities.master;
import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "permission", schema = "masterdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class Permission extends BaseEntity {

    @ManyToOne 
    @JoinColumn(name = "role_id") 
    @JsonBackReference
    private Role role; 

    @Enumerated(EnumType.STRING)
    @Column(name = "module_name", nullable = false) 
    private Module moduleName;

    @Column(name = "can_create")
    private boolean canCreate;

    @Column(name = "can_read")
    private boolean canRead;

    @Column(name = "can_update")
    private boolean canUpdate;

    @Column(name = "can_delete")
    private boolean canDelete;
}