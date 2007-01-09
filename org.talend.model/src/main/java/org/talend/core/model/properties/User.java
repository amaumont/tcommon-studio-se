/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.core.model.properties;

import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.core.model.properties.User#getId <em>Id</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getLogin <em>Login</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getPassword <em>Password</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getFirstName <em>First Name</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getLastName <em>Last Name</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getDeleteDate <em>Delete Date</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#isDeleted <em>Deleted</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#isAllowedToModifyComponents <em>Allowed To Modify Components</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getComment <em>Comment</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getRole <em>Role</em>}</li>
 *   <li>{@link org.talend.core.model.properties.User#getProjectAuthorization <em>Project Authorization</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.core.model.properties.PropertiesPackage#getUser()
 * @model
 * @generated
 */
public interface User extends EObject {
    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(int)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_Id()
     * @model id="true" required="true"
     * @generated
     */
    int getId();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId(int value);

    /**
     * Returns the value of the '<em><b>Login</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Login</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Login</em>' attribute.
     * @see #setLogin(String)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_Login()
     * @model required="true"
     * @generated
     */
    String getLogin();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getLogin <em>Login</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Login</em>' attribute.
     * @see #getLogin()
     * @generated
     */
    void setLogin(String value);

    /**
     * Returns the value of the '<em><b>Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Password</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Password</em>' attribute.
     * @see #setPassword(byte[])
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_Password()
     * @model unique="false" required="true"
     * @generated
     */
    byte[] getPassword();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getPassword <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Password</em>' attribute.
     * @see #getPassword()
     * @generated
     */
    void setPassword(byte[] value);

    /**
     * Returns the value of the '<em><b>First Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>First Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>First Name</em>' attribute.
     * @see #setFirstName(String)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_FirstName()
     * @model unique="false" required="true"
     * @generated
     */
    String getFirstName();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getFirstName <em>First Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>First Name</em>' attribute.
     * @see #getFirstName()
     * @generated
     */
    void setFirstName(String value);

    /**
     * Returns the value of the '<em><b>Last Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Last Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Last Name</em>' attribute.
     * @see #setLastName(String)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_LastName()
     * @model unique="false" required="true"
     * @generated
     */
    String getLastName();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getLastName <em>Last Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Last Name</em>' attribute.
     * @see #getLastName()
     * @generated
     */
    void setLastName(String value);

    /**
     * Returns the value of the '<em><b>Creation Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Creation Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Creation Date</em>' attribute.
     * @see #setCreationDate(Date)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_CreationDate()
     * @model unique="false" required="true"
     * @generated
     */
    Date getCreationDate();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getCreationDate <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Creation Date</em>' attribute.
     * @see #getCreationDate()
     * @generated
     */
    void setCreationDate(Date value);

    /**
     * Returns the value of the '<em><b>Delete Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Delete Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Delete Date</em>' attribute.
     * @see #setDeleteDate(Date)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_DeleteDate()
     * @model unique="false"
     * @generated
     */
    Date getDeleteDate();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getDeleteDate <em>Delete Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Delete Date</em>' attribute.
     * @see #getDeleteDate()
     * @generated
     */
    void setDeleteDate(Date value);

    /**
     * Returns the value of the '<em><b>Deleted</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Deleted</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Deleted</em>' attribute.
     * @see #setDeleted(boolean)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_Deleted()
     * @model unique="false"
     * @generated
     */
    boolean isDeleted();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#isDeleted <em>Deleted</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Deleted</em>' attribute.
     * @see #isDeleted()
     * @generated
     */
    void setDeleted(boolean value);

    /**
     * Returns the value of the '<em><b>Allowed To Modify Components</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Allowed To Modify Components</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Allowed To Modify Components</em>' attribute.
     * @see #setAllowedToModifyComponents(boolean)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_AllowedToModifyComponents()
     * @model unique="false"
     * @generated
     */
    boolean isAllowedToModifyComponents();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#isAllowedToModifyComponents <em>Allowed To Modify Components</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Allowed To Modify Components</em>' attribute.
     * @see #isAllowedToModifyComponents()
     * @generated
     */
    void setAllowedToModifyComponents(boolean value);

    /**
     * Returns the value of the '<em><b>Comment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Comment</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Comment</em>' attribute.
     * @see #setComment(String)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_Comment()
     * @model unique="false"
     * @generated
     */
    String getComment();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getComment <em>Comment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Comment</em>' attribute.
     * @see #getComment()
     * @generated
     */
    void setComment(String value);

    /**
     * Returns the value of the '<em><b>Role</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Role</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Role</em>' reference.
     * @see #setRole(UserRole)
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_Role()
     * @model required="true"
     * @generated
     */
    UserRole getRole();

    /**
     * Sets the value of the '{@link org.talend.core.model.properties.User#getRole <em>Role</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Role</em>' reference.
     * @see #getRole()
     * @generated
     */
    void setRole(UserRole value);

    /**
     * Returns the value of the '<em><b>Project Authorization</b></em>' reference list.
     * The list contents are of type {@link org.talend.core.model.properties.UserProjectAuthorization}.
     * It is bidirectional and its opposite is '{@link org.talend.core.model.properties.UserProjectAuthorization#getUser <em>User</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Project Authorization</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Project Authorization</em>' reference list.
     * @see org.talend.core.model.properties.PropertiesPackage#getUser_ProjectAuthorization()
     * @see org.talend.core.model.properties.UserProjectAuthorization#getUser
     * @model type="org.talend.core.model.properties.UserProjectAuthorization" opposite="user"
     * @generated
     */
    EList getProjectAuthorization();

} // User