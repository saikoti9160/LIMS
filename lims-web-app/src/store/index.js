import { configureStore } from '@reduxjs/toolkit';
import userReducer from './slices/userSlice';
import samplesReducer from './slices/samplesSlice';
import subscriptionReducer from './slices/subscriptionSlice';
import locationReducer from "./slices/locationMasterSlice";

const store = configureStore({
  reducer: {
    user: userReducer,
    samples: samplesReducer,
    subscription: subscriptionReducer,
    locationMaster: locationReducer,
  },
});

export default store;
