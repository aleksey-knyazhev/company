import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { requestJson } from '../api/requestJson';
import { deleteCompany } from './companiesSlice';

export const fetchEmployees = createAsyncThunk('employees/fetchAll', () => requestJson('/employees'));

export const createEmployee = createAsyncThunk('employees/create', (payload) => requestJson('/employees', {
  method: 'POST',
  body: JSON.stringify(payload),
}));

export const updateEmployee = createAsyncThunk('employees/update', ({ id, name, age, companyId }) => requestJson(`/employees/${id}`, {
  method: 'PUT',
  body: JSON.stringify({ name, age, companyId }),
}));

export const deleteEmployee = createAsyncThunk('employees/delete', async (id) => {
  await requestJson(`/employees/${id}`, { method: 'DELETE' });
  return id;
});

const employeesSlice = createSlice({
  name: 'employees',
  initialState: {
    items: [],
    loading: false,
    error: '',
  },
  reducers: {
    clearEmployeeError: (state) => {
      state.error = '';
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchEmployees.pending, (state) => {
        state.loading = true;
        state.error = '';
      })
      .addCase(fetchEmployees.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchEmployees.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(createEmployee.fulfilled, (state, action) => {
        state.items.push(action.payload);
      })
      .addCase(updateEmployee.fulfilled, (state, action) => {
        state.items = state.items.map((employee) => (
          employee.id === action.payload.id ? action.payload : employee
        ));
      })
      .addCase(deleteEmployee.fulfilled, (state, action) => {
        state.items = state.items.filter((employee) => employee.id !== action.payload);
      })
      .addCase(deleteCompany.fulfilled, (state, action) => {
        state.items = state.items.filter((employee) => employee.companyId !== action.payload);
      })
      .addMatcher(
        (action) => action.type.startsWith('employees/') && action.type.endsWith('/rejected'),
        (state, action) => {
          state.error = action.error.message;
        },
      );
  },
});

export const { clearEmployeeError } = employeesSlice.actions;
export default employeesSlice.reducer;
