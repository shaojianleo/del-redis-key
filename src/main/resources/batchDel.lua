local cursor = 0
local keynum = 0
local delnum = 0
local key = KEYS[1]
repeat
    local res = redis.call("SCAN", cursor, "MATCH", key, "COUNT", 10000)
    if (res ~= nil and #res >= 0) then
        redis.replicate_commands()
        cursor = tonumber(res[1])
        local ks = res[2]
        keynum = #ks
        delnum = delnum + keynum
        for i=1,keynum,1 do
            local k = tostring(ks[i])
            local ttl = redis.call('ttl',k)
                if ttl == -1 then
                    redis.call('del',k)
                end
        end
    end
until (cursor <= 0)
return delnum