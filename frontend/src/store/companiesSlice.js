import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { requestJson } from '../api/requestJson';

export const fetchCompanies = createAsyncThunk('companies/fetchAll', () => requestJson('/companies'));

export const createCompany = createAsyncThunk('companies/create', (payload) => requestJson('/companies', {
  method: 'POST',
  body: JSON.stringify(payload),
}));

export const updateCompany = createAsyncThunk('companies/update', ({ id, name }) => requestJson(`/companies/${id}`, {
  method: 'PUT',
  body: JSON.stringify({ name }),
}));

export const deleteCompany = createAsyncThunk('companies/delete', async (id) => {
  await requestJson(`/companies/${id}`, { method: 'DELETE' });
  return id;
});

const companiesSlice = createSlice({
  name: 'companies',
  initialState: {
    items: [],
    loading: false,
    error: '',
  },
  reducers: {
    clearCompanyError: (state) => {
      state.error = '';
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCompanies.pending, (state) => {
        state.loading = true;
        state.error = '';
      })
      .addCase(fetchCompanies.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchCompanies.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(createCompany.fulfilled, (state, action) => {
        state.items.push(action.payload);
      })
      .addCase(updateCompany.fulfilled, (state, action) => {
        state.items = state.items.map((company) => (
          company.id === action.payload.id ? action.payload : company
        ));
      })
      .addCase(deleteCompany.fulfilled, (state, action) => {
        state.items = state.items.filter((company) => company.id !== action.payload);
      })
      .addMatcher(
        (action) => action.type.startsWith('companies/') && action.type.endsWith('/rejected'),
        (state, action) => {
          state.error = action.error.message;
        },
      );
  },
});

export const { clearCompanyError } = companiesSlice.actions;
export default companiesSlice.reducer;
