import { configureStore } from '@reduxjs/toolkit';
import companiesReducer from './companiesSlice';
import employeesReducer from './employeesSlice';

export const store = configureStore({
  reducer: {
    companies: companiesReducer,
    employees: employeesReducer,
  },
});
