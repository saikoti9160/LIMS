const environmentModel = {
    SERVER_NAME: 'local',
    SERVER_SCHEMA: 'http://',
    HOST_NAME: 'localhost',
    PORT: {
        AUTH: '8081',
        LAB_MANAGEMENT: '8082',
        MASTERS: '8083'
    },
}

export const environment = {
    AUTH: `${environmentModel.SERVER_SCHEMA}${environmentModel.HOST_NAME}:${environmentModel.PORT.AUTH}/auth/api/user`,
    LAB_MANAGEMENT: `${environmentModel.SERVER_SCHEMA}${environmentModel.HOST_NAME}:${environmentModel.PORT.LAB_MANAGEMENT}/lab-management/`,
    MASTERS: `${environmentModel.SERVER_SCHEMA}${environmentModel.HOST_NAME}:${environmentModel.PORT.MASTERS}/mdm/`
}