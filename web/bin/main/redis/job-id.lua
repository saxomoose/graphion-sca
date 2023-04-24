-- Redis guarantees the script's atomic execution.
return table.concat(redis.call('TIME'),'-') .. '-' .. redis.call('INCR', 'graphionscaweb:jobs:id')