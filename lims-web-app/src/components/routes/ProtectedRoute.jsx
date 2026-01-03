import React from 'react'
import { usePermissions } from '../../hooks/usePermissions'
import { Route } from 'react-router-dom';
import LandingPage from '../Homepage/LandingPage';

const ProtectedRoute = ({component: Component, requiredPermissions}) => {
    const { hasAnyPermission, permissions } = usePermissions();
    let hasPermissions = [true, true, true, true];
    if (requiredPermissions) {
        hasPermissions = requiredPermissions.some(permission => permissions.includes(permission));
    }
    return (
        <Route render={(props) => <LandingPage {...props} hasPermissions={hasPermissions} />} />
    )
}

export default ProtectedRoute