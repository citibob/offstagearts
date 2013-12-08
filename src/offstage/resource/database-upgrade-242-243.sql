
-- Create a convenience view to show dollar asset only
CREATE OR REPLACE VIEW actrans2_usd AS
select ta.amount,t.*
from actrans2 t inner join actrans2amt ta on (t.actransid = ta.actransid)
inner join assetids on (ta.assetid = assetids.assetid)
where assetids.name = 'USD';

CREATE OR REPLACE VIEW acbal2_usd AS
select ta.bal,t.*
from acbal2 t inner join acbal2amt ta on (t.acbalid = ta.acbalid)
inner join assetids on (ta.assetid = assetids.assetid)
where assetids.name = 'USD';

--drop table actrans;
--drop table acbal;

update mailstateids set name='Valid' where mailstateid=1;
update mailstateids set name='Invalid' where mailstateid=2;
--insert into mailstateids (name) values ('Deceased');

alter table persons add column deceased date;
