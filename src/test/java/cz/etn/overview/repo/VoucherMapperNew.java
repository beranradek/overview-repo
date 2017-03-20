package cz.etn.overview.repo;

import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.AttributeMapping;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.DynamicEntityMapper;

/**
 * @author Radek Beran
 */
public class VoucherMapperNew extends DynamicEntityMapper<Voucher> {

    public VoucherMapperNew() {
//        add(Attribute.of(String.class, "code")
//                .primary()
//                .fromEntity(v -> v.getCode())
//                .toEntity((v, a) -> {
//                    v.setCode(a);
//                    return v;
//                })
//                .build());
    }

    @Override
    public Voucher createEntity() {
        return new Voucher();
    }

    @Override
    public String getTableName() {
        return "voucher";
    }
}
