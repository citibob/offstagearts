-- At this point (2013-12-07), I stopped automating upgrades.
-- All SQL in here has been applied by hand only.

-- 2013-12-07
alter table termregs drop column scholarshippct;
alter table termregs add column regfeescholarship numeric(9, 2) not null default 0;
alter table termregs add column regfee numeric(9,2);
alter table termregs add column defaultregfee numeric(9,2);
alter table termregs add column regfeeoverride numeric(9,2);
alter table courseids add column regfee numeric(9,2);
