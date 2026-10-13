-- Rockford shared storage for Supabase
-- Run this in Supabase SQL Editor. Client apps must use only the anon/publishable key.
-- Never put a service_role key in Android or browser code.

create table if not exists public.rockford_state (
  id integer primary key check (id = 1),
  data jsonb not null,
  updated_at timestamptz not null default now(),
  updated_by uuid references auth.users(id)
);

create table if not exists public.manual_payments (
  id uuid primary key default gen_random_uuid(),
  property_type text not null check (property_type in ('apartment', 'hall')),
  apartment_id integer,
  hall_cage_id integer,
  received_on date not null,
  rent_month text not null,
  amount numeric(12,2) not null check (amount > 0),
  note text not null default '',
  created_at timestamptz not null default now(),
  created_by uuid not null default auth.uid() references auth.users(id),
  updated_at timestamptz not null default now(),
  constraint payment_unit_matches_type check (
    (property_type = 'apartment' and apartment_id is not null and hall_cage_id is null)
    or
    (property_type = 'hall' and hall_cage_id is not null and apartment_id is null)
  )
);

alter table public.rockford_state enable row level security;
alter table public.manual_payments enable row level security;

drop policy if exists "Authenticated users can read Rockford state" on public.rockford_state;
create policy "Authenticated users can read Rockford state"
  on public.rockford_state for select to authenticated using (true);

drop policy if exists "Authenticated users can insert Rockford state" on public.rockford_state;
create policy "Authenticated users can insert Rockford state"
  on public.rockford_state for insert to authenticated with check (auth.uid() is not null);

drop policy if exists "Authenticated users can update Rockford state" on public.rockford_state;
create policy "Authenticated users can update Rockford state"
  on public.rockford_state for update to authenticated using (true) with check (auth.uid() is not null);

drop policy if exists "Authenticated users can read manual payments" on public.manual_payments;
create policy "Authenticated users can read manual payments"
  on public.manual_payments for select to authenticated using (true);

drop policy if exists "Authenticated users can add manual payments" on public.manual_payments;
create policy "Authenticated users can add manual payments"
  on public.manual_payments for insert to authenticated with check (auth.uid() is not null);

drop policy if exists "Authenticated users can update manual payments" on public.manual_payments;
create policy "Authenticated users can update manual payments"
  on public.manual_payments for update to authenticated using (true) with check (auth.uid() is not null);

drop policy if exists "Authenticated users can delete manual payments" on public.manual_payments;
create policy "Authenticated users can delete manual payments"
  on public.manual_payments for delete to authenticated using (auth.uid() is not null);

create index if not exists manual_payments_received_on_idx on public.manual_payments(received_on);
create index if not exists manual_payments_rent_month_idx on public.manual_payments(rent_month);
