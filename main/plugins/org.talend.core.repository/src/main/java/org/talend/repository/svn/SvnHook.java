// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.svn;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;

/**
 * created by wchen on 2013-10-30 Detailled comment
 * 
 */
public interface SvnHook {

    public void unloadAndRemoveResources(Collection<String> changedPaths);

    public Boolean accept(IPath projectRelativePath);
}
