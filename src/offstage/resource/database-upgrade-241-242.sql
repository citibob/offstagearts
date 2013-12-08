-- Allow for shows imports where we don't know what part the kids played
-- alter table shows alter column showroleid drop not null;
-- NO: shows is not in the general schema, it is installation-specific.

-- Bug Fix
CREATE OR REPLACE FUNCTION w_rels_set(xrelid integer, xtemporalid integer, xentityid0 integer, xentityid1 integer)
  RETURNS void AS
'
DECLARE
	xreltype int;
BEGIN
	xreltype := (select reltype from relids where relid = xrelid);

	if xreltype = 0 then		-- many-to-many
		execute w_rels_m2m_set(xrelid, xtemporalid, xentityid0, xentityid1);
	elseif xreltype = 1 then		-- one-to-many
		execute w_rels_o2m_set(xrelid, xtemporalid, xentityid0, xentityid1);
	end if;
END
'
  LANGUAGE plpgsql VOLATILE
  COST 100;


-- Added dated interests
alter table interests add column date date;
